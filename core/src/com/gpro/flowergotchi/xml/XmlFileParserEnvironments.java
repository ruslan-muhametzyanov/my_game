package com.gpro.flowergotchi.xml;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.gpro.flowergotchi.gamelogic.Background;

import java.util.ArrayList;
import java.util.List;

public class XmlFileParserEnvironments extends XmlFileParser {
    public XmlFileParserEnvironments(String file) {
        super(file);
    }

    public List<Background.Parameters> parseEnvTextures() {
        List<Background.Parameters> param = new ArrayList<Background.Parameters>();
        int envCount = root.getChildCount();
        for (int i = 0; i < envCount; ++i) {
            Vector2 p1 = new Vector2();
            Vector2 p2 = new Vector2();
            Element taskElement = root.getChild(i);
            String folder = taskElement.getAttribute("folder");
            String name = taskElement.getAttribute("name");
            p1.add(new Vector2(Float.parseFloat(taskElement.getAttribute("x1")), Float.parseFloat(taskElement.getAttribute("y1"))));
            p2.add(new Vector2(Float.parseFloat(taskElement.getAttribute("x2")), Float.parseFloat(taskElement.getAttribute("y2"))));
            int ambientCount = Integer.parseInt(taskElement.getAttribute("ambientCount"));
            List<String> images = new ArrayList<String>();
            images.add(taskElement.getAttribute("button"));
            images.add(taskElement.getAttribute("background"));
            for (int j = 0; j < taskElement.getChildCount(); ++j) {
                Element child = taskElement.getChild(j);
                images.add(child.getAttribute("background"));
            }
            param.add(new Background.Parameters(images, p1, p2, ambientCount, folder, name, 0.0f));
        }
        return param;
    }
}
