/*
* Copyright 2004,2005 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package org.wso2.carbon.cep.wihidum.core.deployer;


import org.wso2.carbon.automation.api.clients.cep.CEPAdminServiceClient;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.XpathDefinition;
import org.wso2.carbon.cep.core.mapping.input.Input;
import org.wso2.carbon.cep.core.mapping.input.mapping.InputMapping;
import org.wso2.carbon.cep.core.mapping.input.mapping.MapInputMapping;
import org.wso2.carbon.cep.core.mapping.input.mapping.TupleInputMapping;
import org.wso2.carbon.cep.core.mapping.input.mapping.XMLInputMapping;
import org.wso2.carbon.cep.core.mapping.input.property.MapInputProperty;
import org.wso2.carbon.cep.core.mapping.input.property.TupleInputProperty;
import org.wso2.carbon.cep.core.mapping.input.property.XMLInputProperty;
import org.wso2.carbon.cep.core.mapping.output.Output;
import org.wso2.carbon.cep.core.mapping.output.mapping.*;
import org.wso2.carbon.cep.core.mapping.output.property.MapOutputProperty;
import org.wso2.carbon.cep.core.mapping.output.property.TupleOutputProperty;
import org.wso2.carbon.cep.stub.admin.internal.xsd.*;
import org.wso2.carbon.cep.wihidum.core.deployer.client.AuthenticationAdminServiceClient;
import org.wso2.carbon.cep.wihidum.core.deployer.util.ProductConstants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;


public class RemoteBucketDeployer {

    private static String adminCookie;
    private static String authenticationAdminURL;
    private static String cepAdminServiceURL;
    private static final String ADMIN_SERVICE = "AuthenticationAdmin";

    public static void deploy(String ip, Bucket bucketToDeploy) throws Exception {
        BucketDTO bucket = getBucket(bucketToDeploy);
        String serviceURL = "https://" + ip + ":" + ProductConstants.HTTPS_PORT + "/services/";
        authenticationAdminURL = serviceURL + ADMIN_SERVICE;
        cepAdminServiceURL = serviceURL ;

        AuthenticationAdminServiceClient.init(authenticationAdminURL);
        AuthenticationAdminServiceClient.setSystemProperties(new File(".").getCanonicalPath()+ProductConstants.CLIENT_TRUST_STORE_PATH, ProductConstants.KEY_STORE_TYPE, ProductConstants.KEY_STORE_PASSWORD);

        adminCookie = AuthenticationAdminServiceClient.login(ip, ProductConstants.USER_NAME, ProductConstants.PASSWORD);

        if (adminCookie == null) {
             throw new RuntimeException("could not login to the back-end server");
        }

        CEPAdminServiceClient cepAdminServiceClient =  new CEPAdminServiceClient(cepAdminServiceURL,adminCookie);
        cepAdminServiceClient.addBucket(bucket);


    }

    public static void main(String args[]) throws Exception {
        deploy("10.224.7.174",new Bucket());
    }

    public static BucketDTO getBucket(Bucket bucket){

        BucketDTO bucketDTO = new BucketDTO();
        bucketDTO.setName(bucket.getName());
        bucketDTO.setDescription(bucket.getDescription());
        bucketDTO.setEngineProvider(bucket.getEngineProvider());

        Properties properties = bucket.getProviderConfigurationProperties();
        Set<String> namesSet= properties.stringPropertyNames();
        String[] names = namesSet.toArray(new String[namesSet.size()]) ;
        for(String name:names){
            CEPEngineProviderConfigPropertyDTO providerConfigPropertyDTO = new CEPEngineProviderConfigPropertyDTO();
            providerConfigPropertyDTO.setNames(name);
            providerConfigPropertyDTO.setValues(properties.getProperty(name));
            bucketDTO.addEngineProviderConfigProperty(providerConfigPropertyDTO);
        }

        List<Query> queries=bucket.getQueries();

        for(Query query :queries){
            QueryDTO queryDTO = new QueryDTO();
            queryDTO.setName(query.getName());
            queryDTO.setQueryIndex(query.getQueryIndex());

            ExpressionDTO expression = new ExpressionDTO();
            expression.setType("inline");
            expression.setText(query.getExpression().getText());
            queryDTO.setExpression(expression);

            if(query.getOutput() != null){
                OutputDTO outputDTO = new OutputDTO();

                Output output = query.getOutput();
                outputDTO.setTopic(output.getTopic());
                outputDTO.setBrokerName(output.getBrokerName());

                OutputMapping outputMapping = output.getOutputMapping();

                if(outputMapping != null){
                    if(outputMapping instanceof XMLOutputMapping){
                        OutputXMLMappingDTO outputXmlMappingDTO =  new OutputXMLMappingDTO();
                        outputXmlMappingDTO.setMappingXMLText(((XMLOutputMapping) outputMapping).getMappingXMLText());
                        outputDTO.setOutputXmlMapping(outputXmlMappingDTO);
                    }

                    if(outputMapping instanceof TupleOutputMapping){
                        OutputTupleMappingDTO outputTupleMappingDTO =  new OutputTupleMappingDTO();
                        TupleOutputMapping tupleOutputMapping = (TupleOutputMapping) outputMapping;

                        if (tupleOutputMapping.getMetaDataProperties() != null && tupleOutputMapping.getMetaDataProperties().size() != 0) {
                            outputTupleMappingDTO.setMetaDataProperties(tupleOutputMapping.getMetaDataProperties().toArray(new String[tupleOutputMapping.getMetaDataProperties().size()]));
                        }
                        if (tupleOutputMapping.getCorrelationDataProperties() != null && tupleOutputMapping.getCorrelationDataProperties().size() != 0) {
                            outputTupleMappingDTO.setCorrelationDataProperties(tupleOutputMapping.getCorrelationDataProperties().toArray(new String[tupleOutputMapping.getCorrelationDataProperties().size()]));
                        }
                        if (tupleOutputMapping.getPayloadDataProperties() != null && tupleOutputMapping.getPayloadDataProperties().size() != 0) {
                            outputTupleMappingDTO.setPayloadDataProperties(tupleOutputMapping.getPayloadDataProperties().toArray(new String[tupleOutputMapping.getPayloadDataProperties().size()]));
                        }

                       outputDTO.setOutputTupleMapping(outputTupleMappingDTO);

                    }

                    if(outputMapping instanceof MapOutputMapping){
                        OutputMapMappingDTO outputMapMappingDTO =  new OutputMapMappingDTO();
                        MapOutputMapping mapOutputMapping = (MapOutputMapping) outputMapping;

                       if(mapOutputMapping.getPropertyList()!= null && mapOutputMapping.getPropertyList().size()!=0) {
                           outputMapMappingDTO.setProperties(mapOutputMapping.getPropertyList().toArray(new String[mapOutputMapping.getPropertyList().size()]));
                       }

                        outputDTO.setOutputMapMapping(outputMapMappingDTO);

                    }

                }

            }

            bucketDTO.addQueries(queryDTO);

       }

        List<Input> inputs = bucket.getInputs();

        for(Input input :inputs){
            InputDTO inputDTO = new InputDTO();
            inputDTO.setTopic(input.getTopic());
            inputDTO.setBrokerName(input.getBrokerName());

            InputMapping inputMapping = input.getInputMapping();

            if(inputMapping instanceof XMLInputMapping) {
                XMLInputMapping xmlInputMapping = (XMLInputMapping) inputMapping;
                InputXMLMappingDTO inputXMLMappingDTO = adaptInputMapping(xmlInputMapping) ;
                inputDTO.setInputXMLMappingDTO(inputXMLMappingDTO);
            }

            if(inputMapping instanceof TupleInputMapping)   {
                TupleInputMapping tupleInputMapping = (TupleInputMapping) inputMapping;
                InputTupleMappingDTO inputTupleMappingDTO = adaptInputMapping(tupleInputMapping) ;
                inputDTO.setInputTupleMappingDTO(inputTupleMappingDTO);
            }

            if(inputMapping instanceof MapInputMapping) {
                MapInputMapping mapInputMapping = (MapInputMapping) inputMapping;
                InputMapMappingDTO inputMapMappingDTO = adaptInputMapping(mapInputMapping) ;
                inputDTO.setInputMapMappingDTO(inputMapMappingDTO);
            }

               bucketDTO.addInputs(inputDTO);
        }



      //  bucketDTO.setInputs(CEPAdminUtils.adaptInput(bucket.getInputs()));


       return bucketDTO;

    }




    private static InputXMLMappingDTO adaptInputMapping(XMLInputMapping xmlInputMapping){

        InputXMLMappingDTO inputXMLMappingDTO = new InputXMLMappingDTO();
        List<XMLInputProperty> properties = xmlInputMapping.getProperties();

        if(properties != null){

            for(XMLInputProperty property: properties){
                XMLPropertyDTO XMLPropertyDTO = new XMLPropertyDTO();
                XMLPropertyDTO.setName(property.getName());
                XMLPropertyDTO.setType(property.getType());
                XMLPropertyDTO.setXpath(property.getXpath());
                inputXMLMappingDTO.addProperties(XMLPropertyDTO);
            }
        }

        List<XpathDefinition> xpathDefinitions = xmlInputMapping.getXpathDefinitionList();

        if(xpathDefinitions != null){
            for(XpathDefinition xpathDefinition: xpathDefinitions){
                XpathDefinitionDTO xpathDefinitionDTO = new XpathDefinitionDTO();
                xpathDefinitionDTO.setNamespace(xpathDefinition.getNamespace());
                xpathDefinitionDTO.setPrefix(xpathDefinition.getPrefix());
                inputXMLMappingDTO.addXpathDefinition(xpathDefinitionDTO);
            }
        }

        inputXMLMappingDTO.setStream(xmlInputMapping.getStream());
       /*check this*/
        inputXMLMappingDTO.setMappingClass(xmlInputMapping.getMappingClass().getName());
        return inputXMLMappingDTO;

    }

    private static InputTupleMappingDTO adaptInputMapping(TupleInputMapping tupleInputMapping){
        InputTupleMappingDTO inputTupleMappingDTO = new InputTupleMappingDTO();
        List<TupleInputProperty> properties = tupleInputMapping.getProperties();

        if(properties != null){
            for(TupleInputProperty property : properties){
                TuplePropertyDTO tuplePropertyDTO = new TuplePropertyDTO();
                tuplePropertyDTO.setName(property.getName());
                tuplePropertyDTO.setDataType(property.getInputDataType());
                tuplePropertyDTO.setType(property.getType());
                inputTupleMappingDTO.addProperties(tuplePropertyDTO);
            }


        }

        inputTupleMappingDTO.setStream(tupleInputMapping.getStream());
        /*check this*/
        inputTupleMappingDTO.setMappingClass(tupleInputMapping.getMappingClass().getName());

        return inputTupleMappingDTO;

    }

    private static InputMapMappingDTO adaptInputMapping(MapInputMapping mapInputMapping) {
        InputMapMappingDTO inputMapMappingDTO = new InputMapMappingDTO();
        List<MapInputProperty> properties = mapInputMapping.getProperties();

        if(properties != null){
            for(MapInputProperty property : properties){
                MapPropertyDTO mapPropertyDTO = new MapPropertyDTO();
                mapPropertyDTO.setName(property.getName());
                mapPropertyDTO.setType(property.getType());
                inputMapMappingDTO.addProperties(mapPropertyDTO);
            }


        }
        inputMapMappingDTO.setStream(mapInputMapping.getStream());
        /*check this*/
        inputMapMappingDTO.setMappingClass(mapInputMapping.getMappingClass().getName());

        return inputMapMappingDTO;

    }




}


