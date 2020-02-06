package uk.broadoakdata.pdi;

import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

@RestController
@RequestMapping({"/pdi"})
class PdiController {

    @Value("${pdi.plugins.folder}")
    private String pluginFolder;

    PdiService instance = new PdiService();

    @GetMapping({"/execute"})
    @ResponseBody
    public String execute(@RequestParam Map<String, String> requestParams) throws KettleException {
        if (requestParams == null || requestParams.get("filename") == null) {
            return "Check parameters: "+ requestParams.toString();
        }
        String result = instance.run(pluginFolder, requestParams);
        return result;
    }

    @GetMapping({"/sessionid"})
    public String sessionid() throws Exception {
        String result = RequestContextHolder.currentRequestAttributes().getSessionId();
        return result;
    }

}