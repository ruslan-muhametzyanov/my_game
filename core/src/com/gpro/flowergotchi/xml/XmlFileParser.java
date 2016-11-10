package com.gpro.flowergotchi.xml;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.gpro.flowergotchi.gamelogic.GameWorld;

import java.io.IOException;

abstract class XmlFileParser {
    Element root;

    XmlFileParser(String file) {
        try {
            root = new XmlReader().parse(Gdx.files.internal(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
