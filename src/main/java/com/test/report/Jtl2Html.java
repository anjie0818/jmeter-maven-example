package com.test.report;


import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;


public class Jtl2Html {
    private static Logger logger = Logger.getLogger(Jtl2Html.class);
    public static void main(final String[] args) throws Exception {

        final Jtl2Html jtl2Html = new Jtl2Html();
        jtl2Html.parseXml2Html(args[0],args[1]);

//        String a =  "/Users/anjie/dev/Intellij Idea/jmeter-maven-example/target/jmeter/results/";
//        String b ="/Users/anjie/dev/Intellij Idea/jmeter-maven-example/target/jmeter/extentReport/";
//        jtl2Html.parseXml2Html(a,b);

    }

    public void parseXml2Html(String jtlPath,String htmlPath) throws Exception {

        logger.info("jtlPath:"+jtlPath);
        logger.info("htmlPath:"+htmlPath);

        File file = new File(jtlPath);
        File[] list = file.listFiles();
        if (list==null ||list.length==0){
            logger.error("jtlPath路径下不存在 *.jtl文件。生成报告失败");
            return;
        }
        for (File f :list) {
            parseEachJtlFile(f, htmlPath);
        }

    }
    private String getReportConfig(String reportName){
        ExtentReportBean reportBean = new ExtentReportBean();
        reportBean.setTheme("DARK");
        reportBean.setEncoding("utf-8");
        reportBean.setProtocol("HTTP");
        reportBean.setTimelineEnabled(false);
        reportBean.setOfflineMode(true);
        reportBean.setThumbnailForBase64(false);
        reportBean.setDocumentTitle(reportName);
        reportBean.setReportName(reportName);
        reportBean.setTimeStampFormat("MMM dd, yyyy HH:mm:ss a");
        reportBean.setJs("");
        reportBean.setCss("");
        Gson gson = new Gson();
        String reportStr = gson.toJson(reportBean);
        return reportStr;
    }
    private void parseEachJtlFile(File file, String htmlPath) throws DocumentException, IOException {
        //读jtl nodes
        final SAXReader sax = new SAXReader();
        final Document document = sax.read(file);
        final Element root = document.getRootElement();// 获取根节点
        final List<Element> listElement = root.elements();// 所有一级子节点的list

        // 初始化html对象
        ExtentReports extent = new ExtentReports();
        extent.setReportUsesManualConfiguration(true);
        ExtentSparkReporter spark = new ExtentSparkReporter(htmlPath+file.getName());
        spark.loadJSONConfig(getReportConfig(file.getName()));
        extent.attachReporter(spark);
        /**
         *         ExtentTest test1 = extent.createTest("MyTest1");
         *
         */
        for (final Element node : listElement) {// 遍历所有一级子节点,作为每一个测试case
            ExtentTest testCase = extent.createTest(node.attributeValue("lb"));
            String result = node.attributeValue("s");
            if (result.equals("true")){
                testCase.pass("执行通过");
            }else if (result.equals("false")){
                testCase.fail("执行失败");
            }
            setTestCaseTimerStamp(testCase,node);
            //递归node节点下所有节点，如果是httpsample作为testCase的子节点
            sample2TestCase(testCase,node);
        }
        extent.flush();

    }

    private static void setTestCaseTimerStamp(ExtentTest testCase,Element node){
        //获取sample执行时间
        String timeStamp = node.attributeValue("ts");//ts="1621679238696"
        Date startDate = new Date(Long.parseLong(String.valueOf(timeStamp)));
        testCase.getModel().setStartTime(startDate);
        String timeDiff = node.attributeValue("t");
        long entTimeStamp = Long.parseLong(timeStamp)+Long.parseLong(timeDiff);
        Date endDate = new Date(entTimeStamp);
        testCase.getModel().setEndTime(endDate);
    }
    private static String toHeaderFormat(String headerStr){
        return headerStr.replaceAll("\\n","<br>");
    }
    private static String toJsonFormat(String json) {
        String result = "";
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String tmp = gson.toJson(jsonObject);
            result = "<textarea readonly=\"\" class=\"code-block\">"+tmp+"</textarea>";
        }catch (Exception e){
            result = json;
        }

        return result;
    }
    private void sample2TestCase(ExtentTest testCase, Element node) {

        if (node.getName().equals("httpSample")){
            /**
             * 遍历httpsample子节点获取相关参数
             */
            String requestUrl = "";
            String httpMethod = "";
            String queryString = "";
            String responseData = "";
            String requestHeader = "";
            String responseHeader = "";
            for (final Element e1 : node.elements()) {// 遍历所有httpsamile的子节点
                if (e1.getName().equals("java.net.URL")){
                    requestUrl = e1.getTextTrim();
                };
                if (e1.getName().equals("method")){
                    httpMethod = e1.getTextTrim();
                };
                if (e1.getName().equals("requestHeader")){
                    requestHeader = e1.getText();
                };
                if (e1.getName().equals("responseHeader")){
                    responseHeader = e1.getText();
                };
                if (e1.getName().equals("queryString")){
                    queryString = e1.getTextTrim();
                };
                if (e1.getName().equals("responseData")){
                    responseData = e1.getTextTrim();
                };
            }
            ExtentTest subCase = testCase.createNode(node.attributeValue("lb")+" "+"<span class='badge badge-primary'>"+httpMethod+"</span>");
            String result = node.attributeValue("s");
            if (result.equals("true")){
                subCase.pass("执行通过");
            }else if (result.equals("false")){
                subCase.fail("执行失败");
            }
            //设置时间
            setTestCaseTimerStamp(subCase,node);
            subCase.info("<strong>requestUrl</strong>:<br>"+requestUrl);
            subCase.info("<strong>requestHeader</strong>:<br>"+toHeaderFormat(requestHeader));
            subCase.info("<strong>queryString</strong>:<br>"+toJsonFormat(queryString));
            subCase.info("<strong>responseHeader</strong>:<br>"+toHeaderFormat(responseHeader));
            subCase.info("<strong>responseData</strong>:<br>"+toJsonFormat(responseData));

        }

        // 递归遍历当前节点所有的子节点
        final List<Element> listElement = node.elements();// 所有一级子节点的list
        for (final Element e2 : listElement) {// 遍历所有一级子节点
            sample2TestCase(testCase,e2);// 递归
        }
    }

}