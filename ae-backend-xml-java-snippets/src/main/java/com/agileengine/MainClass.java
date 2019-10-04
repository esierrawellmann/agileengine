package com.agileengine;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class MainClass {
    private static Logger LOGGER = LoggerFactory.getLogger(MainClass.class);
    public static void main(String[] args){


       // <platform> <program_path> <input_origin_file_path> <input_other_sample_file_path>
        if(args.length < 3){
            LOGGER.info("Not enough parameters provided please use the following paths");
            LOGGER.info("<platform> <program_path> <input_origin_file_path> <input_other_sample_file_path>");

        }else{
            final String platform;
            final String programPath;
            String originalFile="";
            String otherPath="";
            String optional = null;
            if(args.length >3 ){
                LOGGER.info("args :"+ args[0] +  " " + args[1] +  " " + args[2] +  " " + args[3] +  " " );
                originalFile = args[2];
                otherPath = args[3];
            }
            if(args.length >4 ) {
                LOGGER.info("args :" + args[0] + " " + args[1] + " " + args[2] + " " + args[3] + " " + args[4] + " ");

                optional = args[4];
            }
            try{

                String original = Files.lines(new File(originalFile).toPath(), StandardCharsets.UTF_8).collect(Collectors.joining(System.lineSeparator()));
                String revised = Files.lines(new File(otherPath).toPath(),StandardCharsets.UTF_8).collect(Collectors.joining(System.lineSeparator()));



                new FilesAnalyzer(original,revised,optional);

            }catch (IOException ex){
                LOGGER.error(ex.getMessage());
                ex.printStackTrace();
            }
            catch (SAXException saxEx){
                LOGGER.error(saxEx.getMessage());
                saxEx.printStackTrace();
            }

        }

    }
}
