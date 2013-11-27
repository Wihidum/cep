package org.wso2.carbon.cep.wihidum.core.bucket.splitter.test;

import junit.framework.Assert;
import org.testng.annotations.Test;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.Expression;
import org.wso2.carbon.cep.core.Query;
import org.wso2.carbon.cep.core.mapping.input.Input;
import org.wso2.carbon.cep.core.mapping.input.mapping.TupleInputMapping;
import org.wso2.carbon.cep.core.mapping.input.property.TupleInputProperty;
import org.wso2.carbon.cep.core.mapping.output.Output;
import org.wso2.carbon.cep.core.mapping.output.mapping.TupleOutputMapping;
import org.wso2.carbon.cep.core.mapping.output.property.TupleOutputProperty;
import org.wso2.carbon.cep.wihidum.core.bucket.splitter.JoinSplitter;
import org.wso2.carbon.cep.wihidum.core.bucket.splitter.PatternSplitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: sachini
 * Date: 8/16/13
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class SplitterTester {

    @Test
    public void PatternTest(){

        Bucket bucket = new Bucket();
        bucket.setName("KPIAnalyzer");
        bucket.setDescription("Notifies when a user purchases more then 3 phones for the total price higher than $2500.");
        bucket.setEngineProvider("SiddhiCEPRuntime");

        List<Input> inputs = new ArrayList<Input>() ;
        Input input1 = new Input();
        input1.setTopic("org.wso2.phone.retail.store/1.2.0");
        input1.setBrokerName("localAgentBroker");
        TupleInputMapping inputMapping = new TupleInputMapping();
        inputMapping.setStream("phoneRetailStream");

        List<TupleInputProperty> properties = new ArrayList<TupleInputProperty>();
        TupleInputProperty property1 = new TupleInputProperty();
        property1.setName("quantity");
        property1.setInputName("quantity");
        property1.setType("java.lang.Integer");
        property1.setInputDataType("payloadData");
        properties.add(property1);

        TupleInputProperty property2 = new TupleInputProperty();
        property2.setName("totalPrice");
        property2.setInputName("totalPrice");
        property2.setInputDataType("payloadData");
        property2.setType("java.lang.Integer");
        properties.add(property2);

        inputMapping.setProperties(properties);

        input1.setInputMapping(inputMapping);
        inputs.add(input1) ;
        bucket.setInputs(inputs);

        List<Query> queries = new ArrayList<Query>();
        Query query1 = new Query();
        query1.setName("KPIQuery");

        Expression expression = new Expression();
        expression.setText("from e1 = phoneRetailStream [ totalPrice >= 50 ] -> e2 = phoneRetailStream [quantity <= 50 ] " +
                "   -> e3 = phoneRetailStream [totalPrice <= 70 ] insert into StockQuote ;");
        query1.setExpression(expression);
        query1.addIP("192.168.1.4");
        query1.addIP("192.168.1.5");
        query1.addIP("192.168.1.6");
        query1.addIP("192.168.1.3");

        queries.add(query1);
        query1.setOutput(new Output());
        bucket.setQueries(queries);
        Map<String, Bucket> list = new PatternSplitter().getBucketList(bucket);
        Assert.assertEquals(4,list.size());
        Assert.assertNotNull(list.get("192.168.1.5"));
        Assert.assertNotNull(list.get("192.168.1.4"));
        Assert.assertNotNull(list.get("192.168.1.6"));
        Assert.assertNotNull(list.get("192.168.1.3"));
        Assert.assertEquals(list.get("192.168.1.5").getQueries().get(0).getExpression().getText(), "from  phoneRetailStream [quantity <= 50 ]insert into phoneRetailStreamP quantity , totalPrice ;");
        Assert.assertEquals(list.get("192.168.1.4").getQueries().get(0).getExpression().getText(), "from  phoneRetailStream [ totalPrice >= 50 ]insert into phoneRetailStreamP quantity , totalPrice ;");
        Assert.assertEquals(list.get("192.168.1.6").getQueries().get(0).getExpression().getText(), "from  phoneRetailStream [totalPrice <= 70 ]insert into phoneRetailStreamP quantity , totalPrice ;");


    }


    @Test
    public void JoinTest(){

        Bucket bucket = new Bucket();
        bucket.setName("KPIAnalyzer");
        bucket.setDescription("Notifies when a user purchases more then 3 phones for the total price higher than $2500.");
        bucket.setEngineProvider("SiddhiCEPRuntime");

        List<Input> inputs = new ArrayList<Input>() ;
        Input input1 = new Input();
        input1.setTopic("org.wso2.phone.retail.store/1.2.0");
        input1.setBrokerName("localAgentBroker");
        TupleInputMapping inputMapping = new TupleInputMapping();
        inputMapping.setStream("phoneRetailStream");

        List<TupleInputProperty> properties = new ArrayList<TupleInputProperty>();
        TupleInputProperty property1 = new TupleInputProperty();
        property1.setName("quantity");
        property1.setInputName("quantity");
        property1.setType("java.lang.Integer");
        property1.setInputDataType("payloadData");
        properties.add(property1);

        TupleInputProperty property2 = new TupleInputProperty();
        property2.setName("totalPrice");
        property2.setInputName("total");
        property2.setInputDataType("payloadData");
        property2.setType("java.lang.Integer");
        properties.add(property2);

        inputMapping.setProperties(properties);

        input1.setInputMapping(inputMapping);
        inputs.add(input1) ;
        bucket.setInputs(inputs);

        List<Query> queries = new ArrayList<Query>();
        Query query1 = new Query();
        query1.setName("KPIQuery");

        Expression expression = new Expression();
        expression.setText("from phoneRetailStream[brand=='IBM']#window.length(2000) as t " +
                "join phoneRetailStream2#window.time(500) as n" +
                "on t.brand == n.brand" +
                "insert into JoinStream t.brand as brand ;");
        query1.setExpression(expression);
        query1.addIP("192.168.1.4");
        query1.addIP("192.168.1.5");

        queries.add(query1);
        query1.setOutput(new Output());
        bucket.setQueries(queries);
        Map<String, Bucket> list = new JoinSplitter().getBucketList(bucket);
        Assert.assertEquals(2,list.size());
        Assert.assertNotNull(list.get("192.168.1.5"));
        Assert.assertNotNull(list.get("192.168.1.4"));

    }




}
