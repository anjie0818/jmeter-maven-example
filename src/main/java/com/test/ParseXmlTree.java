package com.test;


import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpRetryException;
import java.util.List;

/**
 * 遍历xml所有节点（包括子节点下还有子节点多层嵌套）
 */
public class ParseXmlTree {


    private static String HTML = "";
    private static String ENDHTML = "</Panel>";
    private static String startHaveSub1 = "<Panel header=\"";
    private static String startHaveSub2 = "\"><Collapse >";

    private static String startNotHaveSub1 = "<Panel header=\"";
    private static String startNotHaveSub2 = "\">";


    private static String endNotLastNode = "</Panel>";
    private static String endLastNode = "</Panel></Collapse>";

    public static void main(final String[] args) {
        final ParseXmlTree test = new ParseXmlTree();
        String reportStr = "";
        try {
            reportStr = test.testGetRoot();
        } catch (final Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        File src = new File("/Users/anjie/dev/Intellij Idea/jmeter-maven-example/report/src/index.js");
        String cont = ReplaceReport.read("/Users/anjie/dev/Intellij Idea/jmeter-maven-example/report/src/index.js");
        System.out.println(cont);
        //对得到的内容进行处理
        cont = cont.replaceAll("jmeterContent", reportStr);
        System.out.println(cont);
        //更新源文件
        System.out.println(ReplaceReport.write(cont, src));
    }

    /**
     * 获取文件的xml对象，然后获取对应的根节点root
     */
    public String testGetRoot() throws Exception {
        final SAXReader sax = new SAXReader();// 创建一个SAXReader对象
        final File xmlFile = new File("/Users/anjie/dev/Intellij Idea/jmeter-maven-example/target/jmeter/results/20210516-test.jtl.xml");// 根据指定的路径创建file对象
        final Document document = sax.read(xmlFile);// 获取document对象,如果文档无节点，则会抛出Exception提前结束
        final Element root = document.getRootElement();// 获取根节点
        int initfloot = 0;
        getNodes(root,initfloot);// 从根节点开始遍历所有节点
        System.out.println("----=========--------========");
        System.out.println(HTML+ENDHTML);
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("/Users/anjie/dev/Intellij Idea/jmeter-maven-example/runoob.xml"));
            out.write(HTML+ENDHTML);
            out.close();
            System.out.println("文件创建成功！");
        } catch (IOException e) {
        }
        return HTML+ENDHTML;
    }

    /**
     * 从指定节点Element node开始,递归遍历其所有子节点
     */
    public void getNodes(final Element node,int floot) {
        floot++;
        System.out.println("-------开始新节点--------"+floot);

        // 当前节点的名称、文本内容和属性
        System.out.println("当前节点名称：" + node.getName());// 当前节点名称
        System.out.println("当前节点的内容：" + node.getTextTrim());// 当前节点内容
        final List<Attribute> listAttr = node.attributes();// 当前节点的所有属性
//        for (final Attribute attr : listAttr) {// 遍历当前节点的所有属性
//            final String name = attr.getName();// 属性名称
//            final String value = attr.getValue();// 属性的值
//            System.out.println("属性名称：" + name + "---->属性值：" + value);
//        }

        // 递归遍历当前节点所有的子节点
        final List<Element> listElement = node.elements();// 所有一级子节点的list
        int size = listElement.size();
        System.out.println("当前节点子节点个数："+ size);
        String nodeShowMessage = node.getName().toString()+":"+node.attributeValue("lb")+" 测试结果："+node.attributeValue("s");
        if (size != 0){ //非子节点
            HTML = HTML + startHaveSub1+nodeShowMessage+startHaveSub2;
        }else { //子节点
            if (node.getTextTrim().replaceAll("\"","").equals("")){
                node.remove(node);
            }else {
                HTML = HTML + startNotHaveSub1+nodeShowMessage+startNotHaveSub2+node.getTextTrim().replaceAll("<","").replaceAll(">","");
            }
        }
        System.out.println("---html---"+HTML);

        for (final Element e : listElement) {// 遍历所有一级子节点

            getNodes(e,floot);// 递归
        }
        System.out.println("-------结束此节点--------"+floot);
        System.out.println("当前节点子节点个数："+ size);
        System.out.println("当前节点名称：" + node.getName());// 当前节点名称
        System.out.println("当前节点的内容：" + node.getTextTrim());// 当前节点内容

        if (node.getParent() != null){
            Element parent = node.getParent();
            List<Element> subs = parent.elements();
            String lastNodeName = subs.get(subs.size() - 1).getName();
            System.out.println("=尾==:"+lastNodeName);
            if (node.equals(subs.get(subs.size() - 1))){
                System.out.println("当前节点是尾节点");
                HTML = HTML + endLastNode;
            }else {
                System.out.println("当前节点非尾节点");
                HTML = HTML + endNotLastNode;
            }
        }else {
            //  首次
        }

    }
    private boolean equalsNode(Element a1,Element a2){
        boolean bl = true;

        if (a1.getName()!=a2.getName()){
            return false;
        }
        return bl;
    }
}