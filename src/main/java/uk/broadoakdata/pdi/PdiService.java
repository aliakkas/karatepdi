package uk.broadoakdata.pdi;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.extension.ExtensionPointHandler;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingBuffer;
import org.pentaho.di.core.plugins.PluginFolder;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class PdiService {

    public static final String XP_PDI_START = "PdiStart";
    public static final String XP_CREATE_ENVIRONMENT = "CreateEnvironment";
    public static final String XP_IMPORT_ENVIRONMENT = "ImportEnvironment";
    private boolean runTransformation;
    private String filename;
    private boolean runJob;
    private boolean useSpace;
    private LogChannel log;
    private VariableSpace space;


    public PdiService() {
    }

    public String run(String pluginFolder, Map<String, String> requestParams)  {
        Environment environment = null;
        useSpace = false;
        String result = "<pdi-result>";
        try {
            this.filename = requestParams.get("filename");
            log = new LogChannel( "PDI" );
            buildVariableSpace();
            StepPluginType.getInstance().getPluginFolders().add(new PluginFolder(pluginFolder, false, true));
            initialize();
            log.logBasic("Embedded PDI started");
            // Allow modification of various environment settings
            //
            ExtensionPointHandler.callExtensionPoint( log, XP_PDI_START, environment );
            /*
            buildMetaStore();
            if (StringUtils.isNotEmpty( createEnvironmentOption )) {
                createEnvironment();
            }
            if (StringUtils.isNotEmpty( environmentJsonFilename )) {
                importEnvironment();
            }
            */
            if ( isTransformation() ) {
                Trans t = runTransformationFromFileSystem(requestParams);
                result = result + t.getResult().getXML();
                LoggingBuffer appender = KettleLogStore.getAppender();
                String logText = "<system-out><![CDATA[" + appender.getBuffer(t.getLogChannelId(), false).toString() + "]]></system-out>";
                result = result + logText + "</pdi-result>";
                appender.clear();
            }
            if ( isJob() ) {
                Job j = runJobFromFileSystem(requestParams);
                result = result + j.getResult().getXML();
                LoggingBuffer appender = KettleLogStore.getAppender();
                String logText = "<system-out><![CDATA[" + appender.getBuffer(j.getLogChannelId(), false).toString() + "]]></system-out>";
                result = result + logText + "</pdi-result>";
                appender.clear();
        }
            return result;
        } catch ( Exception e ) {
            // something went wrong, just log and return
            e.printStackTrace();
            return null;
        }
    }

    private void buildVariableSpace() throws IOException {
        // Load kettle.properties before running for convenience...
        //
        File kettleFile = new File( Const.getKettleDirectory() + "kettle.properties" );
        FileInputStream inputStream;
        if (kettleFile.exists() && kettleFile.isFile()) {
            inputStream = new FileInputStream(Const.getKettleDirectory() + "kettle.properties");
        } else {
            inputStream = new FileInputStream( "etl/kettle.properties");
            useSpace = true;
        }
        space = new Variables();
        space.initializeVariablesFrom( null );
        Properties kettleProperties = new Properties();
        kettleProperties.load( inputStream);
        for ( final String key : kettleProperties.stringPropertyNames() ) {
            space.setVariable( key, kettleProperties.getProperty( key ) );
        }
    }

    private void initialize( ) throws Exception {
        try {
            KettleEnvironment.init(false);
        } catch ( Exception e ) {
            throw new Exception("There was a problem during the initialization of the Kettle environment", e );
        }
    }
    private boolean isTransformation() {
        if ( runTransformation ) {
            return true;
        }
        if (StringUtils.isEmpty( filename )) {
            return false;
        }
        return filename.toLowerCase().endsWith( ".ktr" );
    }

    private boolean isJob() {
        if ( runJob ) {
            return true;
        }
        if (StringUtils.isEmpty( filename )) {
            return false;
        }
        return filename.toLowerCase().endsWith( ".kjb" );
    }

    /**
     * This method executes a job defined in a kjb file
     *
     * It demonstrates the following:
     *
     * - Loading a job definition from a kjb file - Setting named parameters for
     * the job - Setting the log level of the job - Executing the job, waiting
     * for it to finish - Examining the result of the job
     *
     * @return the job that was executed, or null if there was an error
     */
    public Job runJobFromFileSystem(Map<String, String> requestParams) {
        String[] knownOptions = new String[] { "job", "level", };
        String transOption = requestParams.get( "job" );
        String levelOption = requestParams.get( "level" );
        try {
            initialize( );
            // Loading the job file from file system into the JobMeta object.
            // The JobMeta object is the programmatic representation of a job
            // definition.
            JobMeta jobMeta = new JobMeta( filename, null );

            // Set the servlet parameters as variables in the transformation
            //
            String[] parameters = jobMeta.listParameters();
            if (useSpace) {
                for ( String key : space.listVariables() ) {
                    jobMeta.setParameterValue( key, space.getVariable(key) );
                }
            }

            for ( String key : requestParams.keySet() ) {
                // Ignore the known options. set the rest as variables
                //
                if ( Const.indexOfString( key, knownOptions ) < 0 ) {
                    // If it's a trans parameter, set it, otherwise simply set the
                    // variable
                    if ( Const.indexOfString( key, parameters ) < 0 ) {
                        jobMeta.setVariable( key, requestParams.get(key));
                    } else {
                        jobMeta.setParameterValue( key, requestParams.get(key) );
                    }
                }
            }
            // Creating a Job object which is the programmatic representation of
            // a job
            // A Job object can be executed, report success, etc.
            Job job = new Job( null, jobMeta );

            // adjust the log level
            job.setLogLevel( LogLevel.BASIC );

            // starting the job thread, which will execute asynchronously
            job.start();

            // waiting for the job to finish
            job.waitUntilFinished();

            // retrieve the result object, which captures the success of the job
            Result result = job.getResult();

            // report on the outcome of the job
            String outcome = String.format( "\nJob %s executed with result: %s and %d errors\n",
                    filename, result.getResult(), result.getNrErrors() );
            System.out.println( outcome );

            return job;
        } catch ( Exception e ) {
            // something went wrong, just log and return
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method executes a transformation defined in a ktr file
     *
     * @return the transformation that was executed, or null if there was an error
     */
    public Trans runTransformationFromFileSystem(Map<String, String> requestParams) {
        // Options taken from PAN
        //
        String[] knownOptions = new String[] { "trans", "level", };

        String transOption = requestParams.get( "trans" );
        String levelOption = requestParams.get( "level" );

        try {
            initialize( );
            // Loading the transformation file from file system into the TransMeta object.
            // The TransMeta object is the programmatic representation of a transformation definition.
            TransMeta transMeta = new TransMeta( filename, (Repository) null );

            // Set the servlet parameters as variables in the transformation
            //
            String[] parameters = transMeta.listParameters();
            if (useSpace) {
                for ( String key : space.listVariables() ) {
                    transMeta.setParameterValue( key, space.getVariable(key) );
                }
            }

            for ( String key : requestParams.keySet() ) {
                // Ignore the known options. set the rest as variables
                //
                if ( Const.indexOfString( key, knownOptions ) < 0 ) {
                    // If it's a trans parameter, set it, otherwise simply set the
                    // variable
                    //
                    if ( Const.indexOfString( key, parameters ) < 0 ) {
                        transMeta.setVariable( key, requestParams.get(key));
                    } else {
                        transMeta.setParameterValue( key, requestParams.get(key) );
                    }
                }
            }

            // Creating a transformation object which is the programmatic representation of a transformation
            // A transformation object can be executed, report success, etc.
            Trans transformation = new Trans( transMeta );

            // adjust the log level
            transformation.setLogLevel( LogLevel.BASIC );
            System.out.println( "Starting transformation" );

            // starting the transformation, which will execute asynchronously
            transformation.execute( new String[0] );

            // waiting for the transformation to finish
            transformation.waitUntilFinished();

            // retrieve the result object, which captures the success of the transformation
            Result result = transformation.getResult();

            // report on the outcome of the transformation
            String outcome = String.format( "\nTrans %s executed %s", filename,
                    ( result.getNrErrors() == 0 ? "successfully" : "with " + result.getNrErrors() + " errors" ) );
            System.out.println( outcome );

            return transformation;
        } catch ( Exception e ) {

            // something went wrong, just log and return
            e.printStackTrace();
            return null;
        }
    }

 }
