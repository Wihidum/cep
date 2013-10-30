package org.wso2.carbon.cep.admin.internal;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.transport.http.HTTPConstants;
import org.apache.log4j.Logger;
import org.wso2.carbon.authenticator.stub.AuthenticationAdminStub;
import org.wso2.carbon.authenticator.stub.LogoutAuthenticationExceptionException;
import org.wso2.carbon.cep.admin.internal.util.ProductConstants;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.XpathDefinition;
import org.wso2.carbon.cep.core.distributing.loadbalancer.LBOutputNode;
import org.wso2.carbon.cep.core.distributing.loadbalancer.Loadbalancer;
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
import org.wso2.carbon.cep.stub.admin.internal.xsd.BucketDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.CEPEngineProviderConfigPropertyDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.ExpressionDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.InputDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.InputMapMappingDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.InputMapPropertyDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.InputTupleMappingDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.InputTuplePropertyDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.InputXMLMappingDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.InputXMLPropertyDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.LBOutputNodeDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.OutputDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.OutputMapMappingDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.OutputMapPropertyDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.OutputTupleMappingDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.OutputTuplePropertyDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.OutputXMLMappingDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.QueryDTO;
import org.wso2.carbon.cep.stub.admin.internal.xsd.XpathDefinitionDTO;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class CEPAdminRemoteBucketDeployer  {
    private static String adminCookie;
    private static String authenticationAdminURL;
    private static String cepAdminServiceURL;
    private static final String ADMIN_SERVICE = "AuthenticationAdmin";
    private static Logger logger = Logger.getLogger(CEPAdminRemoteBucketDeployer .class);
    private static CEPAdminRemoteBucketDeployer cepAdminRemoteBucketDeployer;

    public static CEPAdminRemoteBucketDeployer getInstance(){
        if(cepAdminRemoteBucketDeployer == null){
           cepAdminRemoteBucketDeployer = new CEPAdminRemoteBucketDeployer();
        }
        return cepAdminRemoteBucketDeployer;

    }




    public  void deploy(String ip, Bucket bucketToDeploy) {


        org.wso2.carbon.cep.stub.admin.internal.xsd.BucketDTO bucket = getBucket(bucketToDeploy);
        String serviceURL = "https://" + ip + ":" + ProductConstants.HTTPS_PORT + "/services/";
        authenticationAdminURL = serviceURL + ADMIN_SERVICE;
        cepAdminServiceURL = serviceURL ;
        AuthenticationAdminStub authenticationAdminStub =null;

        try {
            authenticationAdminStub = new AuthenticationAdminStub(authenticationAdminURL);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        ServiceClient client = authenticationAdminStub._getServiceClient();
        Options options = client.getOptions();
        options.setManageSession(true);

        System.setProperty(org.wso2.carbon.cep.core.internal.util.ProductConstants.TRUSTSTORE, ProductConstants.CLIENT_TRUST_STORE_PATH);
        System.setProperty(org.wso2.carbon.cep.core.internal.util.ProductConstants.TRUSTSTORE_PASSWORD, ProductConstants.KEY_STORE_PASSWORD);
        System.setProperty(org.wso2.carbon.cep.core.internal.util.ProductConstants.TRUSTSTORE_TYPE, ProductConstants.KEY_STORE_TYPE);

        try {


            authenticationAdminStub.login(ProductConstants.USER_NAME, ProductConstants.PASSWORD, ip);
            ServiceContext serviceContext = authenticationAdminStub.
                    _getServiceClient().getLastOperationContext().getServiceContext();
            adminCookie   = (String) serviceContext.getProperty(HTTPConstants.COOKIE_STRING);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        if (adminCookie == null) {

            throw new RuntimeException("could not login to the back-end server");
        }



        CEPAdminServiceStub cepAdminServiceStub = null;
        try {
            cepAdminServiceStub = new CEPAdminServiceStub(cepAdminServiceURL+"CEPAdminService");
        } catch (AxisFault axisFault) {
            logger.error(axisFault.getMessage());
        }
        // AuthenticateStub.authenticateStub(adminCookie, cepAdminServiceStub);
        ServiceClient scrclient = cepAdminServiceStub._getServiceClient();
        Options option = scrclient.getOptions();
        option.setManageSession(true);
        option.setTimeOutInMilliSeconds(5*60*1000);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, adminCookie);
        try {
            cepAdminServiceStub.addBucket(bucket);
            try {
               authenticationAdminStub.logout();
            } catch (LogoutAuthenticationExceptionException e) {
                logger.error(e.getMessage());
            }

        } catch (RemoteException e) {
            logger.error("RemoteException", e);

        } catch (CEPAdminServiceCEPAdminException e) {
            logger.info(e.getMessage());

        }

    }


    public static org.wso2.carbon.cep.stub.admin.internal.xsd.BucketDTO getBucket(Bucket bucket){

        if(bucket.getLoadbalancerList().size()>0){
            org.wso2.carbon.cep.stub.admin.internal.xsd.BucketDTO bucketDTO = new org.wso2.carbon.cep.stub.admin.internal.xsd.BucketDTO();
            bucketDTO.setName(bucket.getName());
            List<Loadbalancer> loadbalancerList = bucket.getLoadbalancerList();
            for(Loadbalancer loadbalancer: loadbalancerList){

                org.wso2.carbon.cep.stub.admin.internal.xsd.LoadbalancerDTO loadbalancerDTO = new org.wso2.carbon.cep.stub.admin.internal.xsd.LoadbalancerDTO();
                List<LBOutputNode> lbOutputNodeDTOList = loadbalancer.getOutputNodeList();
                for(LBOutputNode lbOutputNode:lbOutputNodeDTOList){
                    org.wso2.carbon.cep.stub.admin.internal.xsd.LBOutputNodeDTO lbOutputNodeDTO = new LBOutputNodeDTO();
                    lbOutputNodeDTO.setIp(lbOutputNode.getIp());
                    lbOutputNodeDTO.setPort(lbOutputNode.getPort());
                    loadbalancerDTO.addLbOutputNodeDTOs(lbOutputNodeDTO);
                }

                Properties properties = new Properties();
                properties.setProperty(ProductConstants.DISTRIBUTED_PROCESSING,"false");
                Set<String> namesSet= properties.stringPropertyNames();
                String[] names = namesSet.toArray(new String[namesSet.size()]) ;
                for(String name:names){
                    org.wso2.carbon.cep.stub.admin.internal.xsd.CEPEngineProviderConfigPropertyDTO providerConfigPropertyDTO = new org.wso2.carbon.cep.stub.admin.internal.xsd.CEPEngineProviderConfigPropertyDTO();
                    providerConfigPropertyDTO.setNames(name);
                    providerConfigPropertyDTO.setValues(properties.getProperty(name));
                    bucketDTO.addEngineProviderConfigProperty(providerConfigPropertyDTO);
                }
                bucketDTO.addLoadbalancerDTOs(loadbalancerDTO);
            }
            return bucketDTO;

        }else if(bucket.getLoadbalancerList().size()==0){
            org.wso2.carbon.cep.stub.admin.internal.xsd.BucketDTO bucketDTO = new BucketDTO();
            bucketDTO.setName(bucket.getName());
            bucketDTO.setDescription(bucket.getDescription());
            bucketDTO.setEngineProvider(bucket.getEngineProvider());

            Properties properties = bucket.getProviderConfigurationProperties();
            Set<String> namesSet= properties.stringPropertyNames();
            String[] names = namesSet.toArray(new String[namesSet.size()]) ;
            for(String name:names){
                org.wso2.carbon.cep.stub.admin.internal.xsd.CEPEngineProviderConfigPropertyDTO providerConfigPropertyDTO = new CEPEngineProviderConfigPropertyDTO();
                providerConfigPropertyDTO.setNames(name);
                providerConfigPropertyDTO.setValues(properties.getProperty(name));
                bucketDTO.addEngineProviderConfigProperty(providerConfigPropertyDTO);
            }

            List<Query> queries=bucket.getQueries();

            for(Query query :queries){
                org.wso2.carbon.cep.stub.admin.internal.xsd.QueryDTO queryDTO = new QueryDTO();
                queryDTO.setName(query.getName());
                queryDTO.setQueryIndex(query.getQueryIndex());

                org.wso2.carbon.cep.stub.admin.internal.xsd.ExpressionDTO expression = new ExpressionDTO();
                expression.setType("inline");
                expression.setText(query.getExpression().getText());
                queryDTO.setExpression(expression);

                if(query.getOutput() != null){
                    org.wso2.carbon.cep.stub.admin.internal.xsd.OutputDTO outputDTO = new OutputDTO();

                    Output output = query.getOutput();
                    outputDTO.setTopic(output.getTopic());
                    outputDTO.setBrokerName(output.getBrokerName());

                    OutputMapping outputMapping = output.getOutputMapping();

                    if(outputMapping != null){
                        if(outputMapping instanceof XMLOutputMapping){
                            XMLOutputMapping xmlOutputMapping = (XMLOutputMapping) outputMapping;
                            org.wso2.carbon.cep.stub.admin.internal.xsd.OutputXMLMappingDTO outputXmlMappingDTO = adaptOutputMapping(xmlOutputMapping);
                            outputDTO.setOutputXmlMapping(outputXmlMappingDTO);

                        }

                        if(outputMapping instanceof TupleOutputMapping){
                            TupleOutputMapping tupleOutputMapping = (TupleOutputMapping) outputMapping;
                            org.wso2.carbon.cep.stub.admin.internal.xsd.OutputTupleMappingDTO outputTupleMappingDTO =  adaptOutputMapping(tupleOutputMapping);
                            outputDTO.setOutputTupleMapping(outputTupleMappingDTO);

                        }

                        if(outputMapping instanceof MapOutputMapping){
                            MapOutputMapping mapOutputMapping = (MapOutputMapping) outputMapping;
                            org.wso2.carbon.cep.stub.admin.internal.xsd.OutputMapMappingDTO outputMapMappingDTO =adaptOutputMapping(mapOutputMapping);
                            outputDTO.setOutputMapMapping(outputMapMappingDTO);

                        }

                        queryDTO.setOutput(outputDTO);
                    }

                }

                bucketDTO.addQueries(queryDTO);

            }

            List<Input> inputs = bucket.getInputs();

            for(Input input :inputs){
                org.wso2.carbon.cep.stub.admin.internal.xsd.InputDTO inputDTO = new InputDTO();
                inputDTO.setTopic(input.getTopic());
                inputDTO.setBrokerName(input.getBrokerName());

                InputMapping inputMapping = input.getInputMapping();

                if(inputMapping instanceof XMLInputMapping) {
                    XMLInputMapping xmlInputMapping = (XMLInputMapping) inputMapping;
                    org.wso2.carbon.cep.stub.admin.internal.xsd.InputXMLMappingDTO inputXMLMappingDTO = adaptInputMapping(xmlInputMapping) ;
                    inputDTO.setInputXMLMappingDTO(inputXMLMappingDTO);
                }

                if(inputMapping instanceof TupleInputMapping)   {
                    TupleInputMapping tupleInputMapping = (TupleInputMapping) inputMapping;
                    org.wso2.carbon.cep.stub.admin.internal.xsd.InputTupleMappingDTO inputTupleMappingDTO = adaptInputMapping(tupleInputMapping) ;
                    inputDTO.setInputTupleMappingDTO(inputTupleMappingDTO);
                }

                if(inputMapping instanceof MapInputMapping) {
                    MapInputMapping mapInputMapping = (MapInputMapping) inputMapping;
                    org.wso2.carbon.cep.stub.admin.internal.xsd.InputMapMappingDTO inputMapMappingDTO = adaptInputMapping(mapInputMapping) ;
                    inputDTO.setInputMapMappingDTO(inputMapMappingDTO);
                }

                bucketDTO.addInputs(inputDTO);
            }



            //  bucketDTO.setInputs(CEPAdminUtils.adaptInput(bucket.getInputs()));


            return bucketDTO;
        }
        return null;
    }




    private static org.wso2.carbon.cep.stub.admin.internal.xsd.InputXMLMappingDTO adaptInputMapping(XMLInputMapping xmlInputMapping){

        org.wso2.carbon.cep.stub.admin.internal.xsd.InputXMLMappingDTO inputXMLMappingDTO = new InputXMLMappingDTO();
        List<XMLInputProperty> properties = xmlInputMapping.getProperties();

        if(properties != null){

            for(XMLInputProperty property: properties){

                org.wso2.carbon.cep.stub.admin.internal.xsd.InputXMLPropertyDTO XMLPropertyDTO = new InputXMLPropertyDTO();
                XMLPropertyDTO.setName(property.getName());
                XMLPropertyDTO.setType(property.getType());
                XMLPropertyDTO.setXpath(property.getXpath());
                inputXMLMappingDTO.addProperties(XMLPropertyDTO);
            }
        }

        List<XpathDefinition> xpathDefinitions = xmlInputMapping.getXpathDefinitionList();

        if(xpathDefinitions != null){
            for(XpathDefinition xpathDefinition: xpathDefinitions){
                org.wso2.carbon.cep.stub.admin.internal.xsd.XpathDefinitionDTO xpathDefinitionDTO = new XpathDefinitionDTO();
                xpathDefinitionDTO.setNamespace(xpathDefinition.getNamespace());
                xpathDefinitionDTO.setPrefix(xpathDefinition.getPrefix());
                inputXMLMappingDTO.addXpathDefinition(xpathDefinitionDTO);
            }
        }

        inputXMLMappingDTO.setStream(xmlInputMapping.getStream());
        return inputXMLMappingDTO;

    }

    private static org.wso2.carbon.cep.stub.admin.internal.xsd.InputTupleMappingDTO adaptInputMapping(TupleInputMapping tupleInputMapping){
        org.wso2.carbon.cep.stub.admin.internal.xsd.InputTupleMappingDTO inputTupleMappingDTO = new InputTupleMappingDTO();
        List<TupleInputProperty> properties = tupleInputMapping.getProperties();

        if(properties != null){
            for(TupleInputProperty property : properties){
                org.wso2.carbon.cep.stub.admin.internal.xsd.InputTuplePropertyDTO tuplePropertyDTO = new InputTuplePropertyDTO();
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

    private static org.wso2.carbon.cep.stub.admin.internal.xsd.InputMapMappingDTO adaptInputMapping(MapInputMapping mapInputMapping) {
        org.wso2.carbon.cep.stub.admin.internal.xsd.InputMapMappingDTO inputMapMappingDTO = new InputMapMappingDTO();
        List<MapInputProperty> properties = mapInputMapping.getProperties();

        if(properties != null){
            for(MapInputProperty property : properties){
                org.wso2.carbon.cep.stub.admin.internal.xsd.InputMapPropertyDTO mapPropertyDTO = new InputMapPropertyDTO();
                mapPropertyDTO.setName(property.getName());
                mapPropertyDTO.setInputName(property.getInputName());
                mapPropertyDTO.setType(property.getType());
                inputMapMappingDTO.addProperties(mapPropertyDTO);
            }


        }
        inputMapMappingDTO.setStream(mapInputMapping.getStream());

        return inputMapMappingDTO;

    }

    private static org.wso2.carbon.cep.stub.admin.internal.xsd.OutputMapMappingDTO adaptOutputMapping(MapOutputMapping mapOutputMapping){
        org.wso2.carbon.cep.stub.admin.internal.xsd.OutputMapMappingDTO outputMapMappingDTO =  new OutputMapMappingDTO();

        if(mapOutputMapping.getPropertyList()!= null && mapOutputMapping.getPropertyList().size()!=0) {
            List<MapOutputProperty> outputProperties = mapOutputMapping.getPropertyList();

            for(MapOutputProperty property:outputProperties){
                org.wso2.carbon.cep.stub.admin.internal.xsd.OutputMapPropertyDTO outputMapPropertyDTO = new OutputMapPropertyDTO();
                outputMapPropertyDTO.setName(property.getName());
                outputMapPropertyDTO.setValueOf(property.getValueOf());
                outputMapMappingDTO.addMapProperties(outputMapPropertyDTO);
            }

        }

        return outputMapMappingDTO;

    }

    private static org.wso2.carbon.cep.stub.admin.internal.xsd.OutputTupleMappingDTO adaptOutputMapping(TupleOutputMapping tupleOutputMapping){
        org.wso2.carbon.cep.stub.admin.internal.xsd.OutputTupleMappingDTO outputTupleMappingDTO =  new OutputTupleMappingDTO();

        if (tupleOutputMapping.getMetaDataProperties() != null && tupleOutputMapping.getMetaDataProperties().size() != 0) {
            List<TupleOutputProperty> outputProperties = tupleOutputMapping.getMetaDataProperties();

            for(TupleOutputProperty property:outputProperties){
                org.wso2.carbon.cep.stub.admin.internal.xsd.OutputTuplePropertyDTO outputTuplePropertyDTO = new org.wso2.carbon.cep.stub.admin.internal.xsd.OutputTuplePropertyDTO();
                outputTuplePropertyDTO.setName(property.getName());
                outputTuplePropertyDTO.setType(property.getType());
                outputTuplePropertyDTO.setValueOf(property.getValueOf());
                outputTupleMappingDTO.addMetaDataProperties(outputTuplePropertyDTO);
            }
        }

        if (tupleOutputMapping.getCorrelationDataProperties() != null && tupleOutputMapping.getCorrelationDataProperties().size() != 0) {
            List<TupleOutputProperty> outputProperties = tupleOutputMapping.getCorrelationDataProperties();

            for(TupleOutputProperty property:outputProperties){
                org.wso2.carbon.cep.stub.admin.internal.xsd.OutputTuplePropertyDTO outputTuplePropertyDTO = new org.wso2.carbon.cep.stub.admin.internal.xsd.OutputTuplePropertyDTO();
                outputTuplePropertyDTO.setName(property.getName());
                outputTuplePropertyDTO.setType(property.getType());
                outputTuplePropertyDTO.setValueOf(property.getValueOf());
                outputTupleMappingDTO.addCorrelationDataProperties(outputTuplePropertyDTO);
            }
        }


        if (tupleOutputMapping.getPayloadDataProperties() != null && tupleOutputMapping.getPayloadDataProperties().size() != 0) {
            List<TupleOutputProperty> outputProperties = tupleOutputMapping.getPayloadDataProperties();
            for(TupleOutputProperty property:outputProperties){
                org.wso2.carbon.cep.stub.admin.internal.xsd.OutputTuplePropertyDTO outputTuplePropertyDTO = new OutputTuplePropertyDTO();
                outputTuplePropertyDTO.setName(property.getName());
                outputTuplePropertyDTO.setType(property.getType());
                outputTuplePropertyDTO.setValueOf(property.getValueOf());
                outputTupleMappingDTO.addPayloadDataProperties(outputTuplePropertyDTO);
            }
        }

        return outputTupleMappingDTO;
    }

    private static org.wso2.carbon.cep.stub.admin.internal.xsd.OutputXMLMappingDTO adaptOutputMapping(XMLOutputMapping xmlOutputMapping){
        org.wso2.carbon.cep.stub.admin.internal.xsd.OutputXMLMappingDTO outputXmlMappingDTO =  new OutputXMLMappingDTO();
        outputXmlMappingDTO.setMappingXMLText(xmlOutputMapping.getMappingXMLText());
        return outputXmlMappingDTO;

    }
}
