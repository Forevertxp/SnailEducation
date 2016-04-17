package com.snail.education.ui.helper;

import com.snail.education.bean.ProvinceBean;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tianxiaopeng on 16/1/21.
 */
public class SAXPraserHelper extends DefaultHandler {

    final int ITEM = 0x0005;

    List<ProvinceBean> list;
    ProvinceBean provinceBean;
    int currentState = 0;

    String itemName;
    String attributeName;

    public SAXPraserHelper(String itemName, String attributeName) {
        this.itemName = itemName;
        this.attributeName = attributeName;
    }

    public List<ProvinceBean> getList() {
        return list;
    }

    /*
     * 接口字符块通知
*/
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub
// super.characters(ch, start, length);
        String theString = String.valueOf(ch, start, length);
        if (currentState != 0) {
            provinceBean.setName(theString);
            currentState = 0;
        }
        return;
    }

    /*
     * 接收文档结束通知
*/
    @Override
    public void endDocument() throws SAXException {
        // TODO Auto-generated method stub
        super.endDocument();
    }

    /*
     * 接收标签结束通知
*/
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub
        if (localName.equals(itemName))
            list.add(provinceBean);
    }

    /*
     * 文档开始通知
*/
    @Override
    public void startDocument() throws SAXException {
        // TODO Auto-generated method stub
        list = new ArrayList<ProvinceBean>();
    }

    /*
     * 标签开始通知
*/
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
        provinceBean = new ProvinceBean(0, "", "", 0);
        if (localName.equals(itemName)) {
            for (int i = 0; i < attributes.getLength(); i++) {
                if (attributes.getLocalName(i).equals("ID")) {
                    provinceBean.setId(Long.parseLong(attributes.getValue(i)));
                }
                if (attributes.getLocalName(i).equals(attributeName)) {
                    provinceBean.setName(attributes.getValue(i));
                }
                if (attributes.getLocalName(i).equals("PID")) {
                    provinceBean.setPid(Long.parseLong(attributes.getValue(i)));
                }
            }
            currentState = ITEM;
            return;
        }
        currentState = 0;
        return;
    }
}
