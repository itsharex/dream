package com.moxa.dream.driver.xml.builder.mapper;

import com.moxa.dream.driver.xml.builder.XMLBuilder;
import com.moxa.dream.driver.xml.moudle.XmlConstant;
import com.moxa.dream.driver.xml.moudle.XmlHandler;
import com.moxa.dream.driver.xml.util.XmlUtil;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

public class DestroyBuilder extends XMLBuilder {
    private Destroy destroy;

    public DestroyBuilder(XmlHandler workHandler) {
        super(workHandler);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case XmlConstant.DESTROY:
                destroy = XmlUtil.applyAttributes(Destroy.class, attributes);
                break;
            case XmlConstant.SQLACTION:
                SqlActionBuilder sqlActionBuilder = new SqlActionBuilder(workHandler);
                sqlActionBuilder.startElement(uri, localName, qName, attributes);
                break;
            case XmlConstant.MAPPERACTION:
                MapperActionBuilder mapperActionBuilder = new MapperActionBuilder(workHandler);
                mapperActionBuilder.startElement(uri, localName, qName, attributes);
                break;
            case XmlConstant.SERVICERACTION:
                ServiceActionBuilder serviceActionBuilder = new ServiceActionBuilder(workHandler);
                serviceActionBuilder.startElement(uri, localName, qName, attributes);
                break;
            default:
                throwXmlException(uri, localName, qName, attributes, XmlConstant.DESTROY);
        }
    }

    @Override
    public void characters(String s) {
    }

    @Override
    public Object endElement(String uri, String localName, String qName) {
        return destroy;
    }

    @Override
    public void builder(String uri, String localName, String qName, Object obj) {
        switch (qName) {
            case XmlConstant.SQLACTION:
            case XmlConstant.MAPPERACTION:
            case XmlConstant.SERVICERACTION:
                destroy.actionList.add(obj);
                break;
        }
    }

    static class Destroy {
        private List<Object> actionList = new ArrayList<>();

        public List<Object> getActionList() {
            return actionList;
        }
    }
}
