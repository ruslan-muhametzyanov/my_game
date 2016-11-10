package com.gpro.flowergotchi.xml;

import com.badlogic.gdx.utils.XmlReader.Element;
import com.gpro.flowergotchi.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class XmlFileParserPots extends XmlFileParser {
    public XmlFileParserPots(String file) {
        super(file);
    }

    public List<Pair<String, String>> parsePots() {
        int potCount = root.getChildCount();
        List<Pair<String, String>> list = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < potCount; ++i) {
            Element taskElement = root.getChild(i);
            String potClass = taskElement.getAttribute("class");

            String potImage = taskElement.getAttribute("image");
            list.add(new Pair<String, String>(potClass, potImage));
        }
        return list;
    }
}
