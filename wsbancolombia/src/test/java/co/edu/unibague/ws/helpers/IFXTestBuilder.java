package co.edu.unibague.ws.helpers;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Daniel Alejandro Bravo Torres
 *         MIT License - https://opensource.org/licenses/MIT
 *         Copyright (C) 2016. All Rights Reserved
 */
public class IFXTestBuilder {
    public static <T> T getUnmarshalledResult(Class<T> tClass, String xml){
        T result = null;
        try {
            Unmarshaller unmarshaller = JAXBContext.newInstance(tClass).createUnmarshaller();
            result = (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String getStringRequest(String file){
        String r = "";
        try{
            r = new String(Files.readAllBytes(Paths.get(file)));
        }catch (IOException e){
        }
        return r;
    }
}
