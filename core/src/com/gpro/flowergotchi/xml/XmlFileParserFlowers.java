package com.gpro.flowergotchi.xml;

import com.badlogic.gdx.utils.XmlReader;
import com.gpro.flowergotchi.flowerlogic.Plant;
import com.gpro.flowergotchi.util.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 08.09.2015.
 */
public class XmlFileParserFlowers extends XmlFileParser {
    public XmlFileParserFlowers(String file) {
        super(file);
    }

    public List<Pair<String, Plant.Parameters>> parseFlowers() {
        int flowerCount = root.getChildCount();
        List<Pair<String, Plant.Parameters>> list = new ArrayList<Pair<String, Plant.Parameters>>();
        for (int i = 0; i < flowerCount; ++i) {
            XmlReader.Element taskElement = root.getChild(i);
            String flowerClass = taskElement.getAttribute("class");

            String flowerImage = taskElement.getAttribute("image");
            String flowerName = taskElement.getAttribute("name");
            Plant.Parameters param = new Plant.Parameters(flowerClass, flowerName);
            list.add(new Pair<String, Plant.Parameters>(flowerImage, param));
        }
        return list;
    }
}

