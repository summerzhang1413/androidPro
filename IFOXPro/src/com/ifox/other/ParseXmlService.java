package com.ifox.other;

import java.io.InputStream;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParseXmlService {
	public HashMap<String, String> parseXml(InputStream inStream) throws Exception { 
        HashMap<String, String> hashMap = new HashMap<String, String>(); 
        
        // ʵ����һ���ĵ����������� 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
        // ͨ���ĵ�������������ȡһ���ĵ������� 
        DocumentBuilder builder = factory.newDocumentBuilder(); 
        // ͨ���ĵ�ͨ���ĵ�����������һ���ĵ�ʵ�� 
        Document document = builder.parse(inStream); 
        //��ȡXML�ļ����ڵ� 
        Element root = document.getDocumentElement(); 
        //��������ӽڵ� 
        NodeList childNodes = root.getChildNodes(); 
        
        for (int j = 0; j < childNodes.getLength(); j++) 
        { 
            //�����ӽڵ� 
            Node childNode = (Node) childNodes.item(j); 
            if (childNode.getNodeType() == Node.ELEMENT_NODE) 
            { 
                Element childElement = (Element) childNode;  
                if ("version".equals(childElement.getNodeName())) {  
                    hashMap.put("version",childElement.getFirstChild().getNodeValue()); 
                }else if (("name".equals(childElement.getNodeName()))) { 
                    hashMap.put("name",childElement.getFirstChild().getNodeValue()); 
                }else if (("url".equals(childElement.getNodeName()))) { 
                    hashMap.put("url",childElement.getFirstChild().getNodeValue()); 
                } 
            } 
        } 
        return hashMap; 
    } 
	
	public HashMap<String, String> parseXmlNewsInfo(InputStream inStream) throws Exception { 
        HashMap<String, String> hashMap = new HashMap<String, String>(); 
        
        // ʵ����һ���ĵ����������� 
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
        // ͨ���ĵ�������������ȡһ���ĵ������� 
        DocumentBuilder builder = factory.newDocumentBuilder(); 
        // ͨ���ĵ�ͨ���ĵ�����������һ���ĵ�ʵ�� 
        Document document = builder.parse(inStream); 
        //��ȡXML�ļ����ڵ� 
        Element root = document.getDocumentElement(); 
        //��������ӽڵ� 
        NodeList childNodes = root.getChildNodes(); 
        
        for (int j = 0; j < childNodes.getLength(); j++) 
        { 
            //�����ӽڵ� 
            Node childNode = (Node) childNodes.item(j); 
            if (childNode.getNodeType() == Node.ELEMENT_NODE) 
            { 
                Element childElement = (Element) childNode; 
                if ("imagetitle1".equals(childElement.getNodeName())){  
                    hashMap.put("imagetitle1",childElement.getFirstChild().getNodeValue()); 
                } 
                else if (("imagetitle2".equals(childElement.getNodeName()))) { 
                    hashMap.put("imagetitle2",childElement.getFirstChild().getNodeValue()); 
                } 
                else if (("imagetitle3".equals(childElement.getNodeName()))) { 
                    hashMap.put("imagetitle3",childElement.getFirstChild().getNodeValue()); 
                } 
                else if (("imagetitle4".equals(childElement.getNodeName()))) { 
                    hashMap.put("imagetitle4",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("imagetitle5".equals(childElement.getNodeName()))) { 
                    hashMap.put("imagetitle5",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("title1".equals(childElement.getNodeName()))) { 
                    hashMap.put("title1",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("title2".equals(childElement.getNodeName()))) { 
                    hashMap.put("title2",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("title3".equals(childElement.getNodeName()))) { 
                    hashMap.put("title3",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("title4".equals(childElement.getNodeName()))) { 
                    hashMap.put("title4",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("title5".equals(childElement.getNodeName()))) { 
                    hashMap.put("title5",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("title6".equals(childElement.getNodeName()))) { 
                    hashMap.put("title6",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("date1".equals(childElement.getNodeName()))) { 
                	hashMap.put("date1",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("date2".equals(childElement.getNodeName()))) { 
                	hashMap.put("date2",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("date3".equals(childElement.getNodeName()))) { 
                	hashMap.put("date3",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("date4".equals(childElement.getNodeName()))) { 
                	hashMap.put("date4",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("date5".equals(childElement.getNodeName()))) { 
                	hashMap.put("date5",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("date6".equals(childElement.getNodeName()))) { 
                	hashMap.put("date6",childElement.getFirstChild().getNodeValue()); 
                }         
                else if (("info1".equals(childElement.getNodeName()))) { 
                    hashMap.put("info1",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("info2".equals(childElement.getNodeName()))) { 
                    hashMap.put("info2",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("info3".equals(childElement.getNodeName()))) { 
                    hashMap.put("info3",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("info4".equals(childElement.getNodeName()))) { 
                    hashMap.put("info4",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("info5".equals(childElement.getNodeName()))) { 
                    hashMap.put("info5",childElement.getFirstChild().getNodeValue()); 
                }
                else if (("info6".equals(childElement.getNodeName()))) { 
                    hashMap.put("info6",childElement.getFirstChild().getNodeValue()); 
                }
            } 
        } 
        return hashMap; 
    } 
	
	
}
