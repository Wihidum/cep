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

package org.wso2.carbon.cep.core;

import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.wso2.carbon.automation.api.clients.utils.AuthenticateStub;
import org.wso2.carbon.cep.core.internal.client.AuthenticationAdminServiceClient;
import org.wso2.carbon.cep.core.internal.util.ProductConstants;
import org.wso2.carbon.cep.core.mapping.input.Input;
import org.wso2.carbon.cep.core.mapping.input.mapping.InputMapping;
import org.wso2.carbon.cep.core.mapping.input.mapping.MapInputMapping;
import org.wso2.carbon.cep.core.mapping.input.mapping.TupleInputMapping;
import org.wso2.carbon.cep.core.mapping.input.mapping.XMLInputMapping;
import org.wso2.carbon.cep.core.mapping.input.property.MapInputProperty;
import org.wso2.carbon.cep.core.mapping.input.property.TupleInputProperty;
import org.wso2.carbon.cep.core.mapping.input.property.XMLInputProperty;
import org.wso2.carbon.cep.core.mapping.output.Output;
import org.wso2.carbon.cep.core.mapping.output.mapping.MapOutputMapping;
import org.wso2.carbon.cep.core.mapping.output.mapping.OutputMapping;
import org.wso2.carbon.cep.core.mapping.output.mapping.TupleOutputMapping;
import org.wso2.carbon.cep.core.mapping.output.mapping.XMLOutputMapping;
import org.wso2.carbon.cep.core.mapping.output.property.MapOutputProperty;
import org.wso2.carbon.cep.core.mapping.output.property.TupleOutputProperty;
import org.wso2.carbon.cep.stub.admin.CEPAdminServiceCEPAdminException;
import org.wso2.carbon.cep.stub.admin.CEPAdminServiceStub;
import org.wso2.carbon.cep.stub.admin.internal.xsd.*;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.apache.log4j.Logger;


public class RemoteBucketDeployer {

    private static String adminCookie;
    private static String authenticationAdminURL;
    private static String cepAdminServiceURL;
    private static final String ADMIN_SERVICE = "AuthenticationAdmin";
    private static Logger logger = Logger.getLogger(RemoteBucketDeployer.class);

    public static void deploy(String ip, Bucket bucketToDeploy) throws Exception {


        BucketDTO bucket = getBucket(bucketToDeploy);
        String serviceURL = "https://" + ip + ":" + ProductConstants.HTTPS_PORT + "/services/";
        authenticationAdminURL = serviceURL + ADMIN_SERVICE;
        cepAdminServiceURL = serviceURL ;

        AuthenticationAdminServiceClient.init(authenticationAdminURL);
        AuthenticationAdminServiceClient.setSystemProperties(ProductConstants.CLIENT_TRUST_STORE_PATH, ProductConstants.KEY_STORE_TYPE, ProductConstants.KEY_STORE_PASSWORD);

        adminCookie = AuthenticationAdminServiceClient.login(ip, ProductConstants.USER_NAME, ProductConstants.PASSWORD);
        if (adminCookie == null) {

            throw new RuntimeException("could not login to the back-end server");
        }

<<<<<<< HEAD
//        CEPAdminServiceClient cepAdminServiceClient =  new CEPAdminServiceClient(cepAdminServiceURL,adminCookie);
//        cepAdminServiceClient.addBucket(bucket);
        CEPAdminServiceStub cepAdminServiceStub = new CEPAdminServiceStub(cepAdminServiceURL+"CEPAdminService");
       // AuthenticateStub.authenticateStub(adminCookie, cepAdminServiceStub);
        ServiceClient client = cepAdminServiceStub._getServiceClient();
        Options option = client.getOptions();
        option.setManageSession(true);
        option.setTimeOutInMilliSeconds(5*60*1000);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, adminCookie);
        try {
            cepAdminServiceStub.addBucket(bucket);

        } catch (RemoteException e) {
            logger.error("RemoteException", e);
            throw new RemoteException();
        } catch (CEPAdminServiceCEPAdminException e) {
            logger.info(e.getMessage());
            throw new CEPAdminServiceCEPAdminException("CEPAdminServiceCEPAdminException", e);
        }
=======
        CEPAdminServiceClient cepAdminServiceClient =  new CEPAdminServiceClient(cepAdminServiceURL,adminCookie);

        ResourceAdminServiceClient resourceAdminServiceClient = new ResourceAdminServiceClient();
        cepAdminServiceClient.addBucket(bucket);
>>>>>>> 44a1c184a961cd4069c710d767bfd7ea7a9524ee


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
                        XMLOutputMapping xmlOutputMapping = (XMLOutputMapping) outputMapping;
                        OutputXMLMappingDTO outputXmlMappingDTO = adaptOutputMapping(xmlOutputMapping);
                        outputDTO.setOutputXmlMapping(outputXmlMappingDTO);

                    }

                    if(outputMapping instanceof TupleOutputMapping){
                        TupleOutputMapping tupleOutputMapping = (TupleOutputMapping) outputMapping;
                        OutputTupleMappingDTO outputTupleMappingDTO =  adaptOutputMapping(tupleOutputMapping);
                        outputDTO.setOutputTupleMapping(outputTupleMappingDTO);

                    }

                    if(outputMapping instanceof MapOutputMapping){
                        MapOutputMapping mapOutputMapping = (MapOutputMapping) outputMapping;
                        OutputMapMappingDTO outputMapMappingDTO =adaptOutputMapping(mapOutputMapping);
                        outputDTO.setOutputMapMapping(outputMapMappingDTO);

                    }

                    queryDTO.setOutput(outputDTO);
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

                InputXMLPropertyDTO XMLPropertyDTO = new InputXMLPropertyDTO();
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
       return inputXMLMappingDTO;

    }

    private static InputTupleMappingDTO adaptInputMapping(TupleInputMapping tupleInputMapping){
        InputTupleMappingDTO inputTupleMappingDTO = new InputTupleMappingDTO();
        List<TupleInputProperty> properties = tupleInputMapping.getProperties();

        if(properties != null){
            for(TupleInputProperty property : properties){
                InputTuplePropertyDTO tuplePropertyDTO = new InputTuplePropertyDTO();
                tuplePropertyDTO.setName(property.getName());
                tuplePropertyDTO.setInputName(property.getInputName());
                tuplePropertyDTO.setInputDataType(property.getInputDataType());
                tuplePropertyDTO.setType(property.getType());

                inputTupleMappingDTO.addProperties(tuplePropertyDTO);
            }


        }

        inputTupleMappingDTO.setStream(tupleInputMapping.getStream());

        return inputTupleMappingDTO;

    }

    private static InputMapMappingDTO adaptInputMapping(MapInputMapping mapInputMapping) {
        InputMapMappingDTO inputMapMappingDTO = new InputMapMappingDTO();
        List<MapInputProperty> properties = mapInputMapping.getProperties();

        if(properties != null){
            for(MapInputProperty property : properties){
                InputMapPropertyDTO mapPropertyDTO = new InputMapPropertyDTO();
                mapPropertyDTO.setName(property.getName());
                mapPropertyDTO.setInputName(property.getInputName());
                mapPropertyDTO.setType(property.getType());
                inputMapMappingDTO.addProperties(mapPropertyDTO);
            }


        }
        inputMapMappingDTO.setStream(mapInputMapping.getStream());

        return inputMapMappingDTO;

    }

    private static OutputMapMappingDTO adaptOutputMapping(MapOutputMapping mapOutputMapping){
        OutputMapMappingDTO outputMapMappingDTO =  new OutputMapMappingDTO();

        if(mapOutputMapping.getPropertyList()!= null && mapOutputMapping.getPropertyList().size()!=0) {
            List<MapOutputProperty> outputProperties = mapOutputMapping.getPropertyList();

            for(MapOutputProperty property:outputProperties){
                OutputMapPropertyDTO outputMapPropertyDTO = new OutputMapPropertyDTO();
                outputMapPropertyDTO.setName(property.getName());
                outputMapPropertyDTO.setValueOf(property.getValueOf());
                outputMapMappingDTO.addMapProperties(outputMapPropertyDTO);
            }

        }

        return outputMapMappingDTO;

    }

    private static OutputTupleMappingDTO adaptOutputMapping(TupleOutputMapping tupleOutputMapping){
        OutputTupleMappingDTO outputTupleMappingDTO =  new OutputTupleMappingDTO();

        if (tupleOutputMapping.getMetaDataProperties() != null && tupleOutputMapping.getMetaDataProperties().size() != 0) {
            List<TupleOutputProperty> outputProperties = tupleOutputMapping.getMetaDataProperties();

           for(TupleOutputProperty property:outputProperties){
                OutputTuplePropertyDTO outputTuplePropertyDTO = new OutputTuplePropertyDTO();
                outputTuplePropertyDTO.setName(property.getName());
                outputTuplePropertyDTO.setType(property.getType());
                outputTuplePropertyDTO.setValueOf(property.getValueOf());
                outputTupleMappingDTO.addMetaDataProperties(outputTuplePropertyDTO);
           }
        }

        if (tupleOutputMapping.getCorrelationDataProperties() != null && tupleOutputMapping.getCorrelationDataProperties().size() != 0) {
            List<TupleOutputProperty> outputProperties = tupleOutputMapping.getCorrelationDataProperties();

           for(TupleOutputProperty property:outputProperties){
                OutputTuplePropertyDTO outputTuplePropertyDTO = new OutputTuplePropertyDTO();
                outputTuplePropertyDTO.setName(property.getName());
                outputTuplePropertyDTO.setType(property.getType());
                outputTuplePropertyDTO.setValueOf(property.getValueOf());
                outputTupleMappingDTO.addCorrelationDataProperties(outputTuplePropertyDTO);
            }
        }


        if (tupleOutputMapping.getPayloadDataProperties() != null && tupleOutputMapping.getPayloadDataProperties().size() != 0) {
            List<TupleOutputProperty> outputProperties = tupleOutputMapping.getPayloadDataProperties();
            for(TupleOutputProperty property:outputProperties){
                OutputTuplePropertyDTO outputTuplePropertyDTO = new OutputTuplePropertyDTO();
                outputTuplePropertyDTO.setName(property.getName());
                outputTuplePropertyDTO.setType(property.getType());
                outputTuplePropertyDTO.setValueOf(property.getValueOf());
                outputTupleMappingDTO.addPayloadDataProperties(outputTuplePropertyDTO);
            }
        }

        return outputTupleMappingDTO;
    }

    private static OutputXMLMappingDTO adaptOutputMapping(XMLOutputMapping xmlOutputMapping){
        OutputXMLMappingDTO outputXmlMappingDTO =  new OutputXMLMappingDTO();
        outputXmlMappingDTO.setMappingXMLText(xmlOutputMapping.getMappingXMLText());
        return outputXmlMappingDTO;

    }




}
