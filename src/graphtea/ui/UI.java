// GraphTea Project: http://github.com/graphtheorysoftware/GraphTea
// Copyright (C) 2012 Graph Theory Software Foundation: http://GraphTheorySoftware.com
// Copyright (C) 2008 Mathematical Science Department of Sharif University of Technology
// Distributed under the terms of the GNU General Public License (GPL): http://www.gnu.org/licenses/

/*
 * UI.java
 *
 * Created on March 2, 2005, 9:38 AM
 */
package graphtea.ui;

import graphtea.platform.core.AbstractAction;
import graphtea.platform.core.BlackBoard;
import graphtea.platform.core.exception.ExceptionHandler;
import graphtea.platform.extension.ExtensionLoader;
import graphtea.platform.StaticUtils;
import graphtea.platform.plugin.Plugger;
import graphtea.ui.actions.UIEventHandler;
import graphtea.ui.components.GFrame;
import graphtea.ui.components.utils.GFrameLocationProvider;
import graphtea.ui.extension.UIActionExtensionHandler;
import graphtea.ui.xml.UIHandlerImpl;
import graphtea.ui.xml.UIParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * this class is the base class of UI actions,
 * it contains the methods to load UserInterfaces from XML
 * it contains the methods to access different parts of a UI...
 *
 * @author Azin Azadi
 */
public class UI {
    public static final String name = "GraphUI.UI";

    BlackBoard blackboard = null;

    GFrame frame;


    public HashMap<String, AbstractAction> actions = null;

    /**
     * constructor
     *
     * @param bb
     * @param visible
     */
    public UI(BlackBoard bb, boolean visible) {
        this.blackboard = bb;
        //add the UI to the blackboard so the plugins can get it from blackboard
        bb.setData(name, this);
        frame = new GFrame(blackboard);
        frame.setLocation(GFrameLocationProvider.getLocation());
        frame.setSize(GFrameLocationProvider.getSize());
        frame.validate();
        // frame.pack();
//        frame.setVisible(visible);
        actions = new HashMap<String, AbstractAction>();
        blackboard.setData(UIEventHandler.ACTIONS_MAP, actions);
        //initialize the event handler to handle menu and toolbar events
        new UIEventHandler(blackboard);
        ExtensionLoader.registerExtensionHandler(new UIActionExtensionHandler());
    }

    /**
     * fek mikonam ke in add xml kare plugin ha ro rah bendaze, vali hala kheili
     * ghatiam, in tarif methoda ro ehtemalan taghir baies dad.
     *
     * @param XMLFilePath
     * @throws IOException
     */
    public void addXML(String XMLFilePath, Class resClass) throws IOException, SAXException {
        loadXML(XMLFilePath, resClass);
    }

    public void loadXML(String XMLFilePath, Class resClass) throws IOException, SAXException {
        loadXML(XMLFilePath, resClass, frame);
    }

    //this method is not tested yet
    public void addXMLFromString(String XMLString, Class resClass) throws IOException, SAXException {
        UIHandlerImpl i = new UIHandlerImpl(frame, blackboard, actions, resClass);
        try {
            UIParser.parse(new InputSource(XMLString), i);
        } catch (SAXException e) {
            throw e;
        } catch (ParserConfigurationException e) {
            ExceptionHandler.catchException(e);
        }

    }

    public void loadXML(String XMLFilePath, Class resClass, GFrame frame
    ) throws IOException, SAXException {
        UIHandlerImpl hi = new UIHandlerImpl(frame, blackboard, actions,
                resClass);
        try {
            if (resClass == null)
                UIParser.parse(new InputSource(XMLFilePath), hi);
            else
                UIParser.parse(resClass.getResource(XMLFilePath), hi);
        } catch (SAXException e) {
            throw e;
        } catch (ParserConfigurationException e) {
            ExceptionHandler.catchException(e);
        }
        frame.validate();
    }

    //todo: no usage of this method,
    public static String extractHelpPlugin(BlackBoard blackboard, Plugger plugger, String index, String dest, String filter) {
        try {
            File f = new File(dest);
            if (!f.isDirectory())
                f.mkdir();
            f = new File(dest + "/" + index);
            if (!f.isFile()) {
                JarFile jarFile = new JarFile(plugger.files.get("help"));
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry je = entries.nextElement();

                    if (!je.getName().startsWith(filter))
                        continue;

                    System.out.println("Extracting " + je.getName());
                    String fname = je.getName().substring(filter.length());

                    if (je.isDirectory())
                        (new File(dest + "/" + fname)).mkdir();
                    else {
                        File efile = new File(dest, fname);

                        InputStream in = new BufferedInputStream(jarFile
                                .getInputStream(je));
                        OutputStream out = new BufferedOutputStream(
                                new FileOutputStream(efile));
                        byte[] buffer = new byte[2048];
                        for (; ;) {
                            int nBytes = in.read(buffer);
                            if (nBytes <= 0)
                                break;
                            out.write(buffer, 0, nBytes);
                        }
                        out.flush();
                        out.close();
                        in.close();
                    }
                }
            }
            return f.getAbsolutePath();
        } catch (Exception e) {
            StaticUtils.addExceptiontoLog(e, blackboard);
        }
        return null;
    }


    public GFrame getGFrame() {
        return frame;
    }

//    public void sendUIEvent(UIEventData ued){
//
//    }

    //---------------------------------------------------------------------------------
//    public static GTP
}