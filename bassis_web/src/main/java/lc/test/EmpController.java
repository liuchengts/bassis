package lc.test;

import bassis.bassis_bean.annotation.Autowired;
import org.bassis.tools.json.GsonUtils;
import bassis.bassis_web.annotation.Controller;
import bassis.bassis_web.annotation.RequestMapping;
import org.apache.log4j.Logger;

@Controller("/emp")
public class EmpController {
    private static Logger logger = Logger.getLogger(EmpController.class);
    @Autowired
    private String tel;


    @RequestMapping("/res")
    public String res() {
        logger.info("tel:" + tel);
        return GsonUtils.objectToJson(tel);
    }
}
