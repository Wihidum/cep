package org.wso2.carbon.cep.admin.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cep.admin.internal.exception.CEPAdminException;
import org.wso2.carbon.cep.admin.internal.util.CEPAdminDSHolder;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.BucketBasicInfo;
import org.wso2.carbon.cep.core.CEPServiceInterface;
import org.wso2.carbon.cep.core.Expression;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.XpathDefinition;
import org.wso2.carbon.cep.core.exception.CEPConfigurationException;
import org.wso2.carbon.cep.core.mapping.input.mapping.QueryEventTypeConvertor;
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
import org.wso2.carbon.cep.core.mapping.output.mapping.TupleOutputMapping;
import org.wso2.carbon.cep.core.mapping.output.mapping.XMLOutputMapping;
import org.wso2.carbon.cep.core.mapping.output.property.MapOutputProperty;
import org.wso2.carbon.cep.core.mapping.output.property.TupleOutputProperty;
import org.wso2.carbon.core.AbstractAdmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * this class is published as a web service. so that front end can invoke the
 * methods to invoke the cep engine.
 */
public class CEPAdminService extends AbstractAdmin {
    private static final Log log = LogFactory.getLog(CEPAdminService.class);

    /**
     * This method will add bucketDTO to the CEP Engine
     *
     * @param bucketDTO - Admin module bucketDTO
     */
    public boolean addBucket(BucketDTO bucketDTO) throws CEPAdminException {

        List<Input> backendInputList = new ArrayList<Input>();
        Bucket backEndBucket = new Bucket();

        QueryDTO[] queryDTOs = bucketDTO.getQueries();
        InputDTO[] inputDTOs = bucketDTO.getInputs();
        if (inputDTOs != null) {
            for (InputDTO inputDTO : inputDTOs) {
                backendInputList.add(adaptInput(inputDTO));
            }
        }

        List<Query> backEndQueryList = new ArrayList<Query>();
        if (queryDTOs != null) {
            int queryIndex = 0;
            for (QueryDTO queryDTO : queryDTOs) {
                Query query = adaptQuery(queryDTO);
                query.setQueryIndex(queryIndex);
                backEndQueryList.add(query);
                queryIndex++;
            }
        }

        Properties providerConfig = new Properties();
        if (bucketDTO.getEngineProviderConfigProperty() != null) {
            CEPEngineProviderConfigPropertyDTO[] engineProviderConfigProperty = bucketDTO.getEngineProviderConfigProperty();
            for (int i = 0, cepProviderConfigNamesLength = engineProviderConfigProperty.length; i < cepProviderConfigNamesLength; i++) {
                CEPEngineProviderConfigPropertyDTO providerConfigPropertyDTO = engineProviderConfigProperty[i];
                providerConfig.setProperty(providerConfigPropertyDTO.getNames(), providerConfigPropertyDTO.getValues());
            }
        }

        backEndBucket.setName(bucketDTO.getName());
        backEndBucket.setDescription(bucketDTO.getDescription());
        backEndBucket.setEngineProvider(bucketDTO.getEngineProvider());
        backEndBucket.setProviderConfigurationProperties(providerConfig);
        backEndBucket.setInputs(backendInputList);
        backEndBucket.setQueries(backEndQueryList);

        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        try {
            cepServiceInterface.addBucket(backEndBucket, getAxisConfig());
            return true;
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in Adding bucket to back end ";
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public boolean editBucket(BucketDTO bucketDTO) throws CEPAdminException {

        try {
            List<Input> backendInputList = new ArrayList<Input>();
            Bucket backEndBucket = new Bucket();


            QueryDTO[] queryDTOs = bucketDTO.getQueries();
            InputDTO[] inputDTOs = bucketDTO.getInputs();
            if (inputDTOs != null) {
                for (InputDTO inputDTO : inputDTOs) {
                    backendInputList.add(adaptInput(inputDTO));
                }
            }

            List<Query> backEndQueryList = new ArrayList<Query>();
            if (queryDTOs != null) {
                int queryIndex = 0;
                for (QueryDTO queryDTO : queryDTOs) {
                    Query query = adaptQuery(queryDTO);
                    query.setQueryIndex(queryIndex);
                    backEndQueryList.add(query);
                    queryIndex++;
                }
            }

            Properties providerConfig = new Properties();
            if (bucketDTO.getEngineProviderConfigProperty() != null) {
                CEPEngineProviderConfigPropertyDTO[] engineProviderConfigProperty = bucketDTO.getEngineProviderConfigProperty();
                for (int i = 0, cepProviderConfigNamesLength = engineProviderConfigProperty.length; i < cepProviderConfigNamesLength; i++) {
                    CEPEngineProviderConfigPropertyDTO providerConfigPropertyDTO = engineProviderConfigProperty[i];
                    providerConfig.setProperty(providerConfigPropertyDTO.getNames(), providerConfigPropertyDTO.getValues());
                }
            }


            if (!(inputDTOs == null && queryDTOs == null)) {
                backEndBucket.setName(bucketDTO.getName());
                backEndBucket.setDescription(bucketDTO.getDescription());
                backEndBucket.setEngineProvider(bucketDTO.getEngineProvider());
                backEndBucket.setProviderConfigurationProperties(providerConfig);
                backEndBucket.setInputs(backendInputList);
                backEndBucket.setQueries(backEndQueryList);

                CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();

                cepServiceInterface.editBucket(backEndBucket);
            }
            return true;
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in editing the bucket";
            if (e.getCause().getMessage().contains("Error during creating rule")) {
                errorMessage = "Error in processing the query ";
            }
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }

    }


    /**
     * This method will map CEP Admin module InputDTO Topic to CEP Core module InputDTO
     *
     * @param inputDTO - CEP Admin module inputs
     * @return InputTopic  - CEP Core module InputDTO
     * @throws org.wso2.carbon.cep.admin.internal.exception.CEPAdminException
     *
     */
    private Input adaptInput(InputDTO inputDTO) throws CEPAdminException {

        Input backendInput = new Input();

        backendInput.setTopic(inputDTO.getTopic());
        backendInput.setBrokerName(inputDTO.getBrokerName());

        if (inputDTO.getInputXMLMappingDTO() != null) {
            InputXMLMappingDTO inputXMLMappingDTO = inputDTO.getInputXMLMappingDTO();
            backendInput.setInputMapping(adaptInputMapping(inputXMLMappingDTO));
        } else if (inputDTO.getInputTupleMappingDTO() != null) {
            InputTupleMappingDTO inputTupleMappingDTO = inputDTO.getInputTupleMappingDTO();
            backendInput.setInputMapping(adaptInputMapping(inputTupleMappingDTO));
        } else if (inputDTO.getInputMapMappingDTO() != null) {
            InputMapMappingDTO inputMapMappingDTO = inputDTO.getInputMapMappingDTO();
            backendInput.setInputMapping(adaptInputMapping(inputMapMappingDTO));
        }


        return backendInput;
    }


    /**
     * This method will adapt CEP Admin module InputXMLMappingDTO to CEP Core module InputXMLMappingDTO
     *
     * @param inputXMLMappingDTO - CEP Admin module inputXMLMappingDTO
     * @return Mapping - CEP Core module InputXMLMapping
     * @throws org.wso2.carbon.cep.admin.internal.exception.CEPAdminException
     *
     */

    private InputMapping adaptInputMapping(InputXMLMappingDTO inputXMLMappingDTO)
            throws CEPAdminException {
        XMLInputMapping backendInputMapping = new XMLInputMapping();
        InputXMLPropertyDTO[] InputXMLPropertyDTOs = inputXMLMappingDTO.getProperties();
        List<XMLInputProperty> backendInputPropertyList = new ArrayList<XMLInputProperty>();

        if (InputXMLPropertyDTOs != null) {
            for (InputXMLPropertyDTO inputInputXMLPropertyDTO : InputXMLPropertyDTOs) {
                XMLInputProperty backendInputProperty = new XMLInputProperty();
                backendInputProperty.setName(inputInputXMLPropertyDTO.getName());
                backendInputProperty.setXpath(inputInputXMLPropertyDTO.getXpath());
                backendInputProperty.setType(inputInputXMLPropertyDTO.getType());
                backendInputPropertyList.add(backendInputProperty);
            }
        }

        XpathDefinitionDTO[] xpathDefinitionDTOs = inputXMLMappingDTO.getXpathDefinition();
        if (xpathDefinitionDTOs != null) {
            for (XpathDefinitionDTO xpathDefinitionDTO : xpathDefinitionDTOs) {
                XpathDefinition backEndXpathDefinition = new XpathDefinition();
                backEndXpathDefinition.setNamespace(xpathDefinitionDTO.getNamespace());
                backEndXpathDefinition.setPrefix(xpathDefinitionDTO.getPrefix());
                backendInputMapping.addXpathDefinition(backEndXpathDefinition);
            }
        }

        backendInputMapping.setStream(inputXMLMappingDTO.getStream());
        Class mappingClass;
        try {
            mappingClass = QueryEventTypeConvertor.mappingClassFromType(inputXMLMappingDTO.getQueryEventType());
        } catch (CEPConfigurationException e) {
            String errorMessage = "No class found matching " + inputXMLMappingDTO.getQueryEventType();
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
        backendInputMapping.setMappingClass(mappingClass);
        backendInputMapping.setProperties(backendInputPropertyList);


        return backendInputMapping;

    }

    /**
     * This method will adapt CEP Admin module InputTupleMappingDTO to CEP Core module InputTupleMappingDTO
     *
     * @param inputTupleMappingDTO - CEP Admin module inputTupleMappingDTO
     * @return Mapping - CEP Core module InputTupleMapping
     * @throws org.wso2.carbon.cep.admin.internal.exception.CEPAdminException
     *
     */
    private InputMapping adaptInputMapping(InputTupleMappingDTO inputTupleMappingDTO)
            throws CEPAdminException {
        TupleInputMapping backendInputMapping = new TupleInputMapping();
        InputTuplePropertyDTO[] inputTuplePropertyDTOs = inputTupleMappingDTO.getProperties();
        List<TupleInputProperty> backendInputPropertyList = new ArrayList<TupleInputProperty>();

        if (inputTuplePropertyDTOs != null) {
            for (InputTuplePropertyDTO inputTuplePropertyDTO : inputTuplePropertyDTOs) {
                TupleInputProperty backendInputProperty = new TupleInputProperty();
                backendInputProperty.setName(inputTuplePropertyDTO.getName());
                backendInputProperty.setInputName(inputTuplePropertyDTO.getInputName());
                backendInputProperty.setType(inputTuplePropertyDTO.getType());
                backendInputProperty.setInputDataType(inputTuplePropertyDTO.getInputDataType());
                backendInputPropertyList.add(backendInputProperty);
            }
        }

        backendInputMapping.setStream(inputTupleMappingDTO.getStream());
        Class mappingClass;
        try {
            mappingClass = QueryEventTypeConvertor.mappingClassFromType(inputTupleMappingDTO.getQueryEventType());
        } catch (CEPConfigurationException e) {
            String errorMessage = "No class found matching " + inputTupleMappingDTO.getQueryEventType();
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
        backendInputMapping.setMappingClass(mappingClass);
        backendInputMapping.setProperties(backendInputPropertyList);

        return backendInputMapping;

    }

    /**
     * This method will adapt CEP Admin module InputMapMappingDTO to CEP Core module InputTupleMappingDTO
     *
     * @param inputMapMappingDTO - CEP Admin module inputMapMappingDTO
     * @return Mapping - CEP Core module InputTupleMapping
     * @throws org.wso2.carbon.cep.admin.internal.exception.CEPAdminException
     *
     */
    private InputMapping adaptInputMapping(InputMapMappingDTO inputMapMappingDTO)
            throws CEPAdminException {
        MapInputMapping backendInputMapping = new MapInputMapping();
        InputMapPropertyDTO[] inputMapPropertyDTOs = inputMapMappingDTO.getProperties();
        List<MapInputProperty> backendInputPropertyList = new ArrayList<MapInputProperty>();

        if (inputMapPropertyDTOs != null) {
            for (InputMapPropertyDTO inputMapPropertyDTO : inputMapPropertyDTOs) {
                MapInputProperty backendInputProperty = new MapInputProperty();
                backendInputProperty.setName(inputMapPropertyDTO.getName());
                backendInputProperty.setInputName(inputMapPropertyDTO.getInputName());
                backendInputProperty.setType(inputMapPropertyDTO.getType());
                backendInputPropertyList.add(backendInputProperty);
            }
        }

        backendInputMapping.setStream(inputMapMappingDTO.getStream());
        Class mappingClass;
        try {
            mappingClass = QueryEventTypeConvertor.mappingClassFromType(inputMapMappingDTO.getQueryEventType());
        } catch (CEPConfigurationException e) {
            String errorMessage = "No class found matching " + inputMapMappingDTO.getQueryEventType();
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
        backendInputMapping.setMappingClass(mappingClass);
        backendInputMapping.setProperties(backendInputPropertyList);

        return backendInputMapping;

    }

    /**
     * This method will map CEP Admin module QueryDTO to CEP Core module QueryDTO
     *
     * @param queryDTO - CEP Admin module queryDTO
     * @return Query - CEP Core module query
     */
    private Query adaptQuery(QueryDTO queryDTO) {

        Query backEndQuery = new Query();
        Expression backEndExpression = new Expression();

        backEndExpression.setText(queryDTO.getExpression().getText());

        backEndQuery.setName(queryDTO.getName());
        backEndQuery.setQueryIndex(queryDTO.getQueryIndex());
        backEndQuery.setExpression(backEndExpression);

        if (queryDTO.getOutput() != null) {
            Output backEndOutput = new Output();

            OutputDTO outputDTO = queryDTO.getOutput();
            backEndOutput.setTopic(outputDTO.getTopic());
            backEndOutput.setBrokerName(outputDTO.getBrokerName());


            OutputXMLMappingDTO outputXmlMappingDTO = outputDTO.getOutputXmlMapping();
            if (outputXmlMappingDTO != null) {

                XMLOutputMapping backEndXMLOutputMapping = null;
                if (outputXmlMappingDTO.getMappingXMLText() != null && !outputXmlMappingDTO.getMappingXMLText().equals("")) {
                    backEndXMLOutputMapping = new XMLOutputMapping();
                    backEndXMLOutputMapping.setMappingXMLText(outputXmlMappingDTO.getMappingXMLText());
                }
                backEndOutput.setOutputMapping(backEndXMLOutputMapping);
            }

            OutputTupleMappingDTO outputTupleMappingDTO = outputDTO.getOutputTupleMapping();
            if (outputTupleMappingDTO != null) {
                TupleOutputMapping backEndTupleOutputMapping = null;
                backEndTupleOutputMapping = new TupleOutputMapping();
//                try {
//                    backEndTupleOutputMapping.setStreamTopic(outputDTO.getTopic());
//                } catch (InvalidTopicException e) {
//                    new CEPAdminException(e.getMessage());
//                }
                if (outputTupleMappingDTO.getMetaDataProperties() != null && outputTupleMappingDTO.getMetaDataProperties().length != 0) {
                    backEndTupleOutputMapping.setMetaDataProperties(adaptTupleOutputPropertyList(outputTupleMappingDTO.getMetaDataProperties()));
                }
                if (outputTupleMappingDTO.getCorrelationDataProperties() != null && outputTupleMappingDTO.getCorrelationDataProperties().length != 0) {
                    backEndTupleOutputMapping.setCorrelationDataProperties(adaptTupleOutputPropertyList(outputTupleMappingDTO.getCorrelationDataProperties()));
                }
                if (outputTupleMappingDTO.getPayloadDataProperties() != null && outputTupleMappingDTO.getPayloadDataProperties().length != 0) {
                    backEndTupleOutputMapping.setPayloadDataProperties(adaptTupleOutputPropertyList(outputTupleMappingDTO.getPayloadDataProperties()));
                }
                backEndOutput.setOutputMapping(backEndTupleOutputMapping);
            }

            OutputMapMappingDTO outputMapMappingDTO = outputDTO.getOutputMapMapping();
            if (outputMapMappingDTO != null) {
                MapOutputMapping backEndMapOutputMapping = null;
                if (outputMapMappingDTO.getMapProperties() != null && outputMapMappingDTO.getMapProperties().length != 0) {
                    backEndMapOutputMapping = new MapOutputMapping();
                    backEndMapOutputMapping.setPropertyList(adaptMapOutputPropertyList(outputMapMappingDTO.getMapProperties()));
                }
                backEndOutput.setOutputMapping(backEndMapOutputMapping);
            }
            backEndQuery.setOutput(backEndOutput);
        }
        return backEndQuery;
    }

    /**
     * This method will tuple CEP Admin module outputTuplePropertyDTO to CEP Core module TupleOutputProperty
     *
     * @param outputTuplePropertyDTOs - CEP Admin module Array of outputTuplePropertyDTO
     * @return TupleOutputProperty List - CEP Core module TupleOutputProperty List
     */
    private List<TupleOutputProperty> adaptTupleOutputPropertyList(
            OutputTuplePropertyDTO[] outputTuplePropertyDTOs) {
        List<TupleOutputProperty> tupleOutputProperties = new ArrayList<TupleOutputProperty>();
        for (OutputTuplePropertyDTO outputTuplePropertyDTO : outputTuplePropertyDTOs) {
            tupleOutputProperties.add(new TupleOutputProperty(outputTuplePropertyDTO.getName(), outputTuplePropertyDTO.getValueOf(),outputTuplePropertyDTO.getType()));
        }
        return tupleOutputProperties;

    }
    /**
     * This method will map CEP Admin module outputMapPropertyDTO to CEP Core module MapOutputProperty
     *
     * @param outputMapPropertyDTOs - CEP Admin module Array of outputMapPropertyDTO
     * @return Query - CEP Core module outputMapMapping
     */
    private List<MapOutputProperty> adaptMapOutputPropertyList(
            OutputMapPropertyDTO[] outputMapPropertyDTOs) {
        List<MapOutputProperty> mapOutputProperties = new ArrayList<MapOutputProperty>();
        for (OutputMapPropertyDTO outputMapPropertyDTO : outputMapPropertyDTOs) {
            mapOutputProperties.add(new MapOutputProperty(outputMapPropertyDTO.getName(), outputMapPropertyDTO.getValueOf()));
        }
        return mapOutputProperties;

    }

    /**
     * This method will return all the bucket names from backEnd
     */
    public BucketBasicInfoDTO[] getAllBucketNames(int startingIndex, int maxBucketCount)
            throws CEPConfigurationException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        BucketBasicInfoDTO[] bucketBasicInfoDTOArray = null;

        List<BucketBasicInfo> backEndBucketBasicInfoList =
                cepServiceInterface.getBucketList();
        int resultSetSize = maxBucketCount;
        if ((backEndBucketBasicInfoList.size() - startingIndex) < maxBucketCount) {
            resultSetSize = (backEndBucketBasicInfoList.size() - startingIndex);
        }
        bucketBasicInfoDTOArray = new BucketBasicInfoDTO[resultSetSize];

        int index = 0;
        int basicInfoIndex = 0;
        for (BucketBasicInfo basicInfo : backEndBucketBasicInfoList) {
            if (startingIndex == index || startingIndex < index) {
                BucketBasicInfoDTO bucketBasicInfoDTO = new BucketBasicInfoDTO();
                bucketBasicInfoDTO.setName(basicInfo.getName());
                bucketBasicInfoDTO.setDescription(basicInfo.getDescription());
                bucketBasicInfoDTOArray[basicInfoIndex] = bucketBasicInfoDTO;
                basicInfoIndex++;
                if (basicInfoIndex == maxBucketCount) {
                    break;
                }
            }
            index++;
        }

        return bucketBasicInfoDTOArray;
    }

    /**
     * this method will return the full bucket count
     */
    public int getAllBucketCount() throws CEPConfigurationException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        return cepServiceInterface.getBucketList().size();
    }

    /**
     * This method will return the bucket with the given name from backEnd
     *
     * @param bucketName - Name of the bucket
     */

    public BucketDTO getBucket(String bucketName) throws CEPAdminException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        BucketDTO bucketDTO = new BucketDTO();
        Bucket backEndBucket = null;
        try {
            backEndBucket = cepServiceInterface.getBucket(bucketName);
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in getting the bucketDTO ";
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
        bucketDTO.setName(backEndBucket.getName());
        bucketDTO.setDescription(backEndBucket.getDescription());
        bucketDTO.setEngineProvider(backEndBucket.getEngineProvider());
        bucketDTO.setEngineProviderConfigProperty(adaptEngineProviderConfigs(backEndBucket.getProviderConfigurationProperties()));
        bucketDTO.setInputs(adaptInput(backEndBucket.getInputs()));
        bucketDTO.setQueries(adaptQueries(backEndBucket.getQueries()));

        return bucketDTO;
    }

    /**
     * This method maps CEP Core module input topic list to CEP Admin module InputDTO array
     *
     * @param providerConfiguration
     */
    private CEPEngineProviderConfigPropertyDTO[] adaptEngineProviderConfigs(
            Properties providerConfiguration) {
        CEPEngineProviderConfigPropertyDTO[] cepEngineProviderPropertyDTO = null;
        if (providerConfiguration != null) {
            cepEngineProviderPropertyDTO = new CEPEngineProviderConfigPropertyDTO[providerConfiguration.size()];
            int i = 0;
            for (Map.Entry<Object, Object> entry : providerConfiguration.entrySet()) {
                cepEngineProviderPropertyDTO[i] = new CEPEngineProviderConfigPropertyDTO();
                cepEngineProviderPropertyDTO[i].setNames(entry.getKey().toString());
                if (entry.getValue() instanceof String) {
                    cepEngineProviderPropertyDTO[i].setValues((String) entry.getValue());
                } else {
                    cepEngineProviderPropertyDTO[i].setValues((String) ((List) entry.getValue()).get(0));
                }
                i++;
            }
        }
        return cepEngineProviderPropertyDTO;
    }

    /**
     * This method maps CEP Core module input topic list to CEP Admin module InputDTO array
     *
     * @param backEndInputList - CEP Core module input  list
     * @return CEP Admin module InputDTO  array
     */
    private InputDTO[] adaptInput(List<Input> backEndInputList) {
        InputDTO[] inputDTOs = new InputDTO[backEndInputList.size()];
        int i = 0;
        for (Input backEndInput : backEndInputList) {
            InputDTO inputDTO = new InputDTO();
            inputDTO.setTopic(backEndInput.getTopic());
            inputDTO.setBrokerName(backEndInput.getBrokerName());
            if (backEndInput.getInputMapping() instanceof XMLInputMapping) {
                inputDTO.setInputXMLMappingDTO(adaptMapping((XMLInputMapping) backEndInput.getInputMapping()));
            } else if (backEndInput.getInputMapping() instanceof MapInputMapping) {
                inputDTO.setInputMapMappingDTO(adaptMapping((MapInputMapping) backEndInput.getInputMapping()));
            } else {
                inputDTO.setInputTupleMappingDTO(adaptMapping((TupleInputMapping) backEndInput.getInputMapping()));
            }
            inputDTOs[i] = inputDTO;
            i++;
        }
        return inputDTOs;
    }

    /**
     * This method maps CEP Core module InputXMLMappingDTO to CEP Admin module  Mappings
     *
     * @param backEndXMLInputMapping - CEP Core module input Stream Mappings
     */
    private InputXMLMappingDTO adaptMapping(XMLInputMapping backEndXMLInputMapping) {
        InputXMLMappingDTO inputXMLMappingDTO = new InputXMLMappingDTO();
        inputXMLMappingDTO.setStream(backEndXMLInputMapping.getStream());
        inputXMLMappingDTO.setQueryEventType(QueryEventTypeConvertor.mappingClassToType(backEndXMLInputMapping.getMappingClass()));
        inputXMLMappingDTO.setProperties(adaptXMLInputProperties(backEndXMLInputMapping.getProperties()));
        inputXMLMappingDTO.setXpathDefinition(adaptXpathDefinitions(backEndXMLInputMapping.getXpathNamespacePrefixes()));
        return inputXMLMappingDTO;
    }

    /**
     * This method maps CEP Core module InputTupleMappingDTO to CEP Admin module  Mappings
     *
     * @param backEndTupleInputMapping - CEP Core module input Stream Mappings
     */
    private InputTupleMappingDTO adaptMapping(TupleInputMapping backEndTupleInputMapping) {
        InputTupleMappingDTO inputTupleMappingDTO = new InputTupleMappingDTO();
        inputTupleMappingDTO.setQueryEventType(QueryEventTypeConvertor.mappingClassToType(backEndTupleInputMapping.getMappingClass()));
        inputTupleMappingDTO.setStream(backEndTupleInputMapping.getStream());
        inputTupleMappingDTO.setProperties(adaptTupleInputProperties(backEndTupleInputMapping.getProperties()));
        return inputTupleMappingDTO;
    }

    private InputMapMappingDTO adaptMapping(MapInputMapping backEndMapInputMapping) {
        InputMapMappingDTO inputMapMappingDTO = new InputMapMappingDTO();
        inputMapMappingDTO.setStream(backEndMapInputMapping.getStream());
        inputMapMappingDTO.setQueryEventType(QueryEventTypeConvertor.mappingClassToType(backEndMapInputMapping.getMappingClass()));
        inputMapMappingDTO.setProperties(adaptMapInputProperties(backEndMapInputMapping.getProperties()));
        return inputMapMappingDTO;
    }

    /**
     * This method maps CEP core module InputDTO property list to CEP Admin module input property array
     *
     * @param backEndXMLPropertyList - CEP Core module input property list
     */
    private InputXMLPropertyDTO[] adaptXMLInputProperties(List<XMLInputProperty> backEndXMLPropertyList) {
        InputXMLPropertyDTO[] inputInputXMLPropertyDTOs = new InputXMLPropertyDTO[backEndXMLPropertyList.size()];
        int i = 0;
        for (XMLInputProperty backEndInputProperty : backEndXMLPropertyList) {
            InputXMLPropertyDTO InputXMLPropertyDTO = new InputXMLPropertyDTO();
            InputXMLPropertyDTO.setName(backEndInputProperty.getName());
            InputXMLPropertyDTO.setXpath(backEndInputProperty.getXpath());
            InputXMLPropertyDTO.setType(backEndInputProperty.getType());
            inputInputXMLPropertyDTOs[i] = InputXMLPropertyDTO;
            i++;
        }
        return inputInputXMLPropertyDTOs;
    }


    /**
     * This method maps CEP core module InputDTO property list to CEP Admin module input property array
     *
     * @param backEndTuplePropertyList - CEP Core module input property list
     */
    private InputTuplePropertyDTO[] adaptTupleInputProperties(
            List<TupleInputProperty> backEndTuplePropertyList) {
        InputTuplePropertyDTO[] inputInputTuplePropertyDTOs = new InputTuplePropertyDTO[backEndTuplePropertyList.size()];
        int i = 0;
        for (TupleInputProperty backEndInputProperty : backEndTuplePropertyList) {
            InputTuplePropertyDTO inputTuplePropertyDTO = new InputTuplePropertyDTO();
            inputTuplePropertyDTO.setName(backEndInputProperty.getName());
            inputTuplePropertyDTO.setInputName(backEndInputProperty.getInputName());
            inputTuplePropertyDTO.setInputDataType(backEndInputProperty.getInputDataType());
            inputTuplePropertyDTO.setType(backEndInputProperty.getType());
            inputInputTuplePropertyDTOs[i] = inputTuplePropertyDTO;
            i++;
        }
        return inputInputTuplePropertyDTOs;
    }

    /**
     * This method maps CEP core module InputDTO property list to CEP Admin module input property array
     *
     * @param backEndMapPropertyList - CEP Core module input property list
     */
    private InputMapPropertyDTO[] adaptMapInputProperties(List<MapInputProperty> backEndMapPropertyList) {
        InputMapPropertyDTO[] inputInputMapPropertyDTOs = new InputMapPropertyDTO[backEndMapPropertyList.size()];
        int i = 0;
        for (MapInputProperty backEndInputProperty : backEndMapPropertyList) {
            InputMapPropertyDTO InputMapPropertyDTO = new InputMapPropertyDTO();
            InputMapPropertyDTO.setName(backEndInputProperty.getName());
            InputMapPropertyDTO.setInputName(backEndInputProperty.getInputName());
            InputMapPropertyDTO.setType(backEndInputProperty.getType());
            inputInputMapPropertyDTOs[i] = InputMapPropertyDTO;
            i++;
        }
        return inputInputMapPropertyDTOs;
    }

    /**
     * This method maps CEP Core module nameSpacePrefixes Map in to CEP Admin module NamespacePrefixDTO array
     *
     * @param backEndXpathDefinitions
     */
    private XpathDefinitionDTO[] adaptXpathDefinitions(
            List<XpathDefinition> backEndXpathDefinitions) {
        XpathDefinitionDTO[] xpathDefinitionDTOs = new XpathDefinitionDTO[backEndXpathDefinitions.size()];
        int i = 0;
        for (XpathDefinition xpathDefinition : backEndXpathDefinitions) {
            XpathDefinitionDTO xpathDefinitionDTO = new XpathDefinitionDTO();
            xpathDefinitionDTO.setPrefix(xpathDefinition.getPrefix());
            xpathDefinitionDTO.setNamespace(xpathDefinition.getNamespace());
            xpathDefinitionDTOs[i] = xpathDefinitionDTO;
            i++;
        }
        return xpathDefinitionDTOs;
    }

    /**
     * This method maps CEP Core module QueryDTO list in to CEP Admin module QueryDTO array
     *
     * @param backEndQueryList
     */
    private QueryDTO[] adaptQueries(List<Query> backEndQueryList) {
        QueryDTO[] queryDTOs = new QueryDTO[backEndQueryList.size()];
        int index = 0;
        for (Query backEndQuery : backEndQueryList) {
            QueryDTO queryDTO = new QueryDTO();
            queryDTO.setName(backEndQuery.getName());
            queryDTO.setQueryIndex(backEndQuery.getQueryIndex());
            if (backEndQuery.getOutput() != null) {
                queryDTO.setOutput(adaptOutput(backEndQuery.getOutput()));
            }
            queryDTO.setExpression(adaptExpression(backEndQuery.getExpression()));
            queryDTOs[index] = queryDTO;
            index++;
        }
        return queryDTOs;
    }

    /**
     * This method maps CEP Core module OutputDTO in to CEP Admin module OutputDTO Topic
     *
     * @param backEndOutput
     */
    private OutputDTO adaptOutput(Output backEndOutput) {

        OutputDTO outputDTO = new OutputDTO();
        outputDTO.setTopic(backEndOutput.getTopic());
        outputDTO.setBrokerName(backEndOutput.getBrokerName());
        if (backEndOutput.getOutputMapping() instanceof TupleOutputMapping) {
            outputDTO.setOutputTupleMapping(adaptOutputTupleMapping((TupleOutputMapping) backEndOutput.getOutputMapping()));
        } else if (backEndOutput.getOutputMapping() instanceof MapOutputMapping) {
            outputDTO.setOutputMapMapping(adaptOutputMapMapping((MapOutputMapping) backEndOutput.getOutputMapping()));
        } else {
            outputDTO.setOutputXmlMapping(adaptOutputXMLMapping((XMLOutputMapping) backEndOutput.getOutputMapping()));
        }
        return outputDTO;
    }


    /**
     * This method will map CEP Core module OutputTupleMappingDTO  in to CEP Admin module ElementMappings
     *
     * @param backEndTupleOutputMapping
     */
    private OutputTupleMappingDTO adaptOutputTupleMapping(
            TupleOutputMapping backEndTupleOutputMapping) {
        OutputTupleMappingDTO outputTupleMappingDTO = null;
        if (backEndTupleOutputMapping != null) {
            outputTupleMappingDTO = new OutputTupleMappingDTO();
            if (backEndTupleOutputMapping.getMetaDataProperties() != null) {
                outputTupleMappingDTO.setMetaDataProperties(adaptOutputTuplePropertyDTOs(backEndTupleOutputMapping.getMetaDataProperties()));
            } else {
                outputTupleMappingDTO.setMetaDataProperties(new OutputTuplePropertyDTO[0]);
            }
            if (backEndTupleOutputMapping.getCorrelationDataProperties() != null) {
                outputTupleMappingDTO.setCorrelationDataProperties(adaptOutputTuplePropertyDTOs(backEndTupleOutputMapping.getCorrelationDataProperties()));
            } else {
                outputTupleMappingDTO.setCorrelationDataProperties(new OutputTuplePropertyDTO[0]);
            }
            if (backEndTupleOutputMapping.getPayloadDataProperties() != null) {
                outputTupleMappingDTO.setPayloadDataProperties(adaptOutputTuplePropertyDTOs(backEndTupleOutputMapping.getPayloadDataProperties()));
            } else {
                outputTupleMappingDTO.setPayloadDataProperties(new OutputTuplePropertyDTO[0]);
            }
        }
        return outputTupleMappingDTO;
    }

    /**
     * This method will tuple CEP Admin module tupleOutputProperty list to CEP Core module OutputTuplePropertyDTO Array
     *
     * @param outputTuplePropertyList - CEP Admin module List of tupleOutputProperty
     * @return OutputTuplePropertyDTO[] - CEP Core module outputTuplePropertyDTOs
     */
    private OutputTuplePropertyDTO[]  adaptOutputTuplePropertyDTOs(
            List<TupleOutputProperty> outputTuplePropertyList) {
        OutputTuplePropertyDTO[] outputTuplePropertyDTOs = new OutputTuplePropertyDTO[outputTuplePropertyList.size()];
        for (int i = 0, outputTuplePropertyListSize = outputTuplePropertyList.size(); i < outputTuplePropertyListSize; i++) {
            TupleOutputProperty tupleOutputProperty = outputTuplePropertyList.get(i);
            OutputTuplePropertyDTO outputTuplePropertyDTO =new OutputTuplePropertyDTO();
            outputTuplePropertyDTO.setName(tupleOutputProperty.getName());
            outputTuplePropertyDTO.setValueOf(tupleOutputProperty.getValueOf());
            outputTuplePropertyDTOs[i]=outputTuplePropertyDTO;
        }
        return outputTuplePropertyDTOs;

    }

    /**
     * This method will map CEP Core module OutputMapMappingDTO  in to CEP Admin module ElementMappings
     *
     * @param backEndMapOutputMapping
     */
    private OutputMapMappingDTO adaptOutputMapMapping(
            MapOutputMapping backEndMapOutputMapping) {
        OutputMapMappingDTO outputMapMappingDTO = null;
        if (backEndMapOutputMapping != null) {
            outputMapMappingDTO = new OutputMapMappingDTO();
            if (backEndMapOutputMapping.getPropertyList() != null) {
                outputMapMappingDTO.setMapProperties(adaptOutputMapPropertyDTOs(backEndMapOutputMapping.getPropertyList()));
            } else {
                outputMapMappingDTO.setMapProperties(new OutputMapPropertyDTO[0]);
            }
        }
        return outputMapMappingDTO;
    }

    /**
     * This method will map CEP Admin module mapOutputProperty list to CEP Core module OutputMapPropertyDTO Array
     *
     * @param outputMapPropertyList - CEP Admin module List of mapOutputProperty
     * @return OutputMapPropertyDTO[] - CEP Core module outputMapPropertyDTOs
     */
    private OutputMapPropertyDTO[]  adaptOutputMapPropertyDTOs(
            List<MapOutputProperty> outputMapPropertyList) {
        OutputMapPropertyDTO[] outputMapPropertyDTOs = new OutputMapPropertyDTO[outputMapPropertyList.size()];
        for (int i = 0, outputMapPropertyListSize = outputMapPropertyList.size(); i < outputMapPropertyListSize; i++) {
            MapOutputProperty mapOutputProperty = outputMapPropertyList.get(i);
            OutputMapPropertyDTO outputMapPropertyDTO =new OutputMapPropertyDTO();
            outputMapPropertyDTO.setName(mapOutputProperty.getName());
            outputMapPropertyDTO.setValueOf(mapOutputProperty.getValueOf());
            outputMapPropertyDTOs[i]=outputMapPropertyDTO;
        }
        return outputMapPropertyDTOs;

    }

    /**
     * This method will map CEP Core module OutputXMLMappingDTO in to CEP Admin module OutputXMLMappingDTO
     *
     * @param backEndXmlOutputMapping
     */
    private OutputXMLMappingDTO adaptOutputXMLMapping(XMLOutputMapping backEndXmlOutputMapping) {
        OutputXMLMappingDTO outputXmlMappingDTO = null;
        if (backEndXmlOutputMapping != null && backEndXmlOutputMapping.getMappingXMLText().length() > 0) {
            outputXmlMappingDTO = new OutputXMLMappingDTO();
            String xmlMappingText = backEndXmlOutputMapping.getMappingXMLText();
            outputXmlMappingDTO.setMappingXMLText(xmlMappingText);
        }
        return outputXmlMappingDTO;
    }

    /**
     * This method maps CEP core module ExpressionDTO to CEP Admin module ExpressionDTO
     *
     * @param backendExpression
     */
    private ExpressionDTO adaptExpression(Expression backendExpression) {
        ExpressionDTO expressionDTO = new ExpressionDTO();
        expressionDTO.setText(backendExpression.getText());
        return expressionDTO;
    }

    /**
     * This method will remove the bucket with the provided bucket name from the back end
     *
     * @param bucketName - name of the bucket to be removed
     * @return status - status of the operation
     */

    public boolean removeBucket(String bucketName) throws CEPAdminException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        try {
            return cepServiceInterface.removeBucket(bucketName);
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in removing the bucket from back end";
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public boolean removeAllBuckets() throws CEPAdminException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        try {
            return cepServiceInterface.removeAllBuckets();
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in removing all buckets from back end ";
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public CEPEngineProviderInfoDTO[] getEngineProvidersInfo() throws CEPAdminException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        try {
            CEPEngineProviderInfoDTO[] info = new CEPEngineProviderInfoDTO[cepServiceInterface.getCEPEngineProviders().length];
            String[] cepEngineProviders = cepServiceInterface.getCEPEngineProviders();
            for (int i = 0, cepEngineProvidersLength = cepEngineProviders.length; i < cepEngineProvidersLength; i++) {
                String providerName = cepEngineProviders[i];
                info[i] = new CEPEngineProviderInfoDTO();
                info[i].setProviderName(providerName);
                info[i].setConfigNames(cepServiceInterface.getCEPEngineProviderConfigNames(providerName));
            }
            return info;
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in getting engine providers from back end ";
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public String[] getBrokerNames() throws CEPAdminException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        try {
            return cepServiceInterface.getCEPBrokerNames();
        } catch (CEPConfigurationException e) {
            throw new CEPAdminException("Error in getting engine providers from back end : " + e);
        }
    }

    public boolean removeQuery(String bucketName, String queryName) throws CEPAdminException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        try {
            return cepServiceInterface.removeQuery(bucketName, queryName);
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in removing query " + queryName + " from bucket :" + bucketName;
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public boolean removeAllQueries(String bucketName) throws CEPAdminException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        try {
            return cepServiceInterface.removeAllQueries(bucketName);
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in removing all queries from bucket :" + bucketName;
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public boolean editQuery(String bucketName, QueryDTO queryDTO) throws CEPAdminException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        try {
            return cepServiceInterface.editQuery(bucketName, adaptQuery(queryDTO));
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in editing the queryDTO :" + queryDTO.getName();
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public QueryDTO[] getAllQueries(String bucketName, int startingIndex, int maxQueryCount)
            throws CEPAdminException {
        try {
            CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
            QueryDTO[] queriesArray = null;
            List<Query> backEndQueryList =
                    cepServiceInterface.getBucket(bucketName).getQueries();
            QueryDTO[] operationsQueryDTOs = null;
            operationsQueryDTOs = adaptQueries(backEndQueryList);

            int resultSetSize = maxQueryCount;
            if ((backEndQueryList.size() - startingIndex) < maxQueryCount) {
                resultSetSize = (backEndQueryList.size() - startingIndex);
            }
            queriesArray = new QueryDTO[resultSetSize];

            int index = 0;
            int queryIndex = 0;
            for (QueryDTO queryDTO : operationsQueryDTOs) {
                if (startingIndex == index || startingIndex < index) {
                    queriesArray[queryIndex] = queryDTO;
                    queryIndex++;
                    if (queryIndex == maxQueryCount) {
                        break;
                    }
                }
                index++;
            }

            return queriesArray;
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in reading queries from back end ";
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public int getAllQueryCount(String bucketName) throws CEPAdminException {
        try {
            CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
            Bucket bucket = cepServiceInterface.getBucket(bucketName);
            return bucket.getQueries().size();
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in getting all query count for the bucket :" + bucketName;
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public InputDTO[] getAllInputs(String bucketName, int startingIndex, int maxQueryCount)
            throws CEPAdminException {
        try {
            CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
            InputDTO[] inputsArray = null;
            List<Input> backEndInputList =
                    cepServiceInterface.getBucket(bucketName).getInputs();
            InputDTO[] operationsInputDTOs = null;
            operationsInputDTOs = adaptInput(backEndInputList);

            int resultSetSize = maxQueryCount;
            if ((backEndInputList.size() - startingIndex) < maxQueryCount) {
                resultSetSize = (backEndInputList.size() - startingIndex);
            }
            inputsArray = new InputDTO[resultSetSize];

            int index = 0;
            int inputIndex = 0;
            for (InputDTO inputDTO : operationsInputDTOs) {
                if (startingIndex == index || startingIndex < index) {
                    inputsArray[inputIndex] = inputDTO;
                    inputIndex++;
                    if (inputIndex == maxQueryCount) {
                        break;
                    }
                }
                index++;
            }

            return inputsArray;
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in reading inputs from back end ";
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public int getAllInputCount(String bucketName) throws CEPAdminException {
        try {
            CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
            Bucket bucket = cepServiceInterface.getBucket(bucketName);
            return bucket.getInputs().size();
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in getting all query count for the bucket :" + bucketName;
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public boolean removeInput(String bucketName, String inputTopic) throws CEPAdminException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        try {
            return cepServiceInterface.removeInput(bucketName, inputTopic);
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in removing input " + inputTopic + " from bucket :" + bucketName;
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }

    public boolean removeAllInputs(String bucketName) throws CEPAdminException {
        CEPServiceInterface cepServiceInterface = CEPAdminDSHolder.getInstance().getCEPService();
        try {
            return cepServiceInterface.removeAllInputs(bucketName);
        } catch (CEPConfigurationException e) {
            String errorMessage = "Error in removing all inputs from bucket :" + bucketName;
            log.error(errorMessage, e);
            throw new CEPAdminException(errorMessage, e);
        }
    }
}

