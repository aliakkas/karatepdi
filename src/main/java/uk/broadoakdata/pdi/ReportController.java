package uk.broadoakdata.pdi;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.io.FileUtils;
import com.intuit.karate.core.Engine;
import com.intuit.karate.core.Feature;
import com.intuit.karate.core.FeatureParser;
import com.intuit.karate.core.FeatureResult;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.presentation.PresentationMode;
import net.masterthought.cucumber.sorting.SortingMethod;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
@RequestMapping({"/karate"})
public class ReportController {

    @GetMapping("/reportxml")
    public String karateXML(@RequestParam() String name) {
        String contents = resultXml(name);
        return "redirect:/";
    }

    @GetMapping("/reporthtml")
    public String karateHtml(@RequestParam() String name) {
        String contents = resultHtml(name);
        return "redirect:/";
    }

    @GetMapping("/reportjson")
    public String karateJson(@RequestParam() String name) {
        String contents = resultJson(name);
        return "redirect:/";
    }

    @GetMapping("/report")
    public String generatePrettyReport(@RequestParam() String name) throws IOException {
        String karateOutputPath = "etl/reports";
        String jsonFile =  resultJson(name);
        Collection<File> jsonFiles = FileUtils.listFiles(new File(karateOutputPath), new String[] {"json"}, true);
        List jsonPaths = new ArrayList(jsonFiles.size());
        for (File file : jsonFiles) {
            jsonPaths.add(file.getAbsolutePath());
        }
        String buildNumber = "RC 1.0.0";
        String projectName = "ST end-to-end testing";
        Configuration configuration = new Configuration(new File(karateOutputPath), projectName);
        configuration.setBuildNumber(buildNumber);
        configuration.setSortingMethod(SortingMethod.NATURAL);
        configuration.addPresentationModes(PresentationMode.EXPAND_ALL_STEPS);
        configuration.setTrendsStatsFile(new File("etl/pdi-trends.json"));
        ReportBuilder reportBuilder = new ReportBuilder(jsonPaths, configuration);
        reportBuilder.generateReports();
        return "redirect:/";
    }

    private static String resultXml(String name) {
        Feature feature = FeatureParser.parse(new File (name));
        FeatureResult result = Engine.executeFeatureSync(null, feature, null, null);
        File file = Engine.saveResultXml("etl/reports", result, null);
        return file.getName();
    }

    private static String resultHtml(String name) {
        Feature feature = FeatureParser.parse(new File (name));
        FeatureResult result = Engine.executeFeatureSync(null, feature, null, null);
        File file = Engine.saveResultHtml("etl/reports", result, null);
        return file.getName();
    }

    private static String resultJson(String name) {
        //http://127.0.0.1:8999/runreport?name=/Users/aliakkas/Downloads/admin/upload-dir/table.feature
        Feature feature = FeatureParser.parse(new File (name));
        FeatureResult result = Engine.executeFeatureSync(null, feature, null, null);
        File file = Engine.saveResultJson("etl/reports", result, null);
        return file.getName();
    }

}