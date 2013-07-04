/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.siddhi.test.standard;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.exception.OperationNotSupportedException;
import org.wso2.siddhi.core.query.output.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.query.api.QueryFactory;
import org.wso2.siddhi.query.api.condition.Condition;
import org.wso2.siddhi.query.api.definition.Attribute;
import org.wso2.siddhi.query.api.exception.MalformedAttributeException;
import org.wso2.siddhi.query.api.expression.Expression;
import org.wso2.siddhi.query.api.query.Query;
import org.wso2.siddhi.query.compiler.exception.SiddhiPraserException;

public class FilterTestCase {
    static final Logger log = Logger.getLogger(FilterTestCase.class);

    private int count;
    private boolean eventArrived;

    @Before
    public void init() {
        count = 0;
        eventArrived = false;
    }

    @Test
    public void testFilterQuery1() throws InterruptedException {

        log.info("Filter test1");
        SiddhiManager siddhiManager = new SiddhiManager();

        InputHandler inputHandler = siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("cseEventStream").ip("12.21.1.1").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(QueryFactory.inputStream("cseEventStream"));
        query.insertInto("StockQuote");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol", Expression.variable("symbol")).
                        project("price", Expression.variable("price")).
                        project("volume", Expression.variable("volume"))
        );

        String queryReference = siddhiManager.addQuery(query);

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertTrue("IBM".equals(inEvents[0].getData(0)) || "WSO2".equals(inEvents[0].getData(0)));
                count++;
            }
        });
//        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"IBM", 75.6f, 100});
        inputHandler.send(new Object[]{"WSO2", 75.6f, 100});
        Thread.sleep(500);
        Assert.assertEquals(2, count);
        siddhiManager.shutdown();

    }

    @Test
    public void testFilterQuery2() throws InterruptedException {
        log.info("Filter test2");
        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("cseEventStream").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(QueryFactory.inputStream("cseEventStream"));
        query.insertInto("StockQuote");

        String queryReference = siddhiManager.addQuery(query);

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertTrue("IBM".equals(inEvents[0].getData(0)) || "WSO2".equals(inEvents[0].getData(0)));
                count++;
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"IBM", 75.6f, 100});
        inputHandler.send(new Object[]{"WSO2", 75.6f, 100});
        Thread.sleep(500);
        Assert.assertEquals(2, count);
        siddhiManager.shutdown();

    }

    @Test
    public void testFilterQuery3() throws InterruptedException {
        log.info("Filter test3");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("cseEventStream").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(QueryFactory.inputStream("cseEventStream"));
        query.insertInto("StockQuote");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol", Expression.variable("symbol"))
        );

        String queryReference = siddhiManager.addQuery(query);

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertTrue("IBM".equals(inEvents[0].getData(0)) || "WSO2".equals(inEvents[0].getData(0)));
                count++;
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"IBM", 75.6f, 100});
        inputHandler.send(new Object[]{"WSO2", 75.6f, 100});
        Thread.sleep(500);
        Assert.assertEquals(2, count);
        siddhiManager.shutdown();

    }

    @Test
    public void testFilterQuery4() throws InterruptedException {
        log.info("Filter test4");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("cseEventStream").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(QueryFactory.inputStream("cseEventStream").
                filter(Condition.compare(Expression.value(70),
                                          Condition.Operator.GREATER_THAN,
                                          Expression.variable("price"))
                )
        );
        query.insertInto("StockQuote");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol", Expression.variable("symbol")).
                        project("price", Expression.variable("price"))
        );

        String queryReference = siddhiManager.addQuery(query);

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertTrue("WSO2".equals(inEvents[0].getData(0)));
                count++;
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"WSO2", 55.6f, 100});
        inputHandler.send(new Object[]{"IBM", 75.6f, 100});
        inputHandler.send(new Object[]{"WSO2", 57.6f, 100});
        Thread.sleep(500);
        Assert.assertEquals(2, count);
        siddhiManager.shutdown();

    }


    @Test
    public void testFilterQuery5() throws InterruptedException {


        log.info("Filter test5");
        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("cseEventStream").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.INT));

        Query query = QueryFactory.createQuery();
        query.from(QueryFactory.inputStream("cseEventStream"));
        query.insertInto("StockQuote");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol", Expression.variable("symbol")).
                        project("price", Expression.variable("price")).
                        project("volume", Expression.variable("volume")).groupBy("symbol")
        );

        String queryReference = siddhiManager.addQuery(query);

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertTrue("IBM".equals(inEvents[0].getData(0)) || "WSO2".equals(inEvents[0].getData(0)));
                count++;
                eventArrived = true;
            }
        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"IBM", 75.6f, 100});
        inputHandler.send(new Object[]{"WSO2", 75.6f, 100});
        Thread.sleep(500);
        Assert.assertEquals(2, count);
        Assert.assertEquals("Event arrived", true, eventArrived);
        siddhiManager.shutdown();

    }

    @Test
    public void testFilterQuery6() throws InterruptedException {

        log.info("Filter test6");
        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream " +
                                                       "insert into StockQuote symbol, price, sum(volume) as sumVolume " +
                                                       "group by symbol;");

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertTrue("IBM".equals(inEvents[0].getData(0)) || "WSO2".equals(inEvents[0].getData(0)));
                count++;
                eventArrived = true;
            }
        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"IBM", 75.6f, 100});
        inputHandler.send(new Object[]{"WSO2", 75.6f, 100});
        Thread.sleep(500);
        Assert.assertEquals(0, count);
        Assert.assertEquals("No events should arrive", false, eventArrived);
        siddhiManager.shutdown();

    }

    @Test
    public void testFilterQuery7() throws InterruptedException {

        log.info("Filter test7");
        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume int) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream " +
                                                       "insert into StockQuote symbol, volume/1000 as vol, sum(volume) as sumVolume " +
                                                       "group by symbol;");

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertTrue("IBM".equals(inEvents[0].getData(0)) || "WSO2".equals(inEvents[0].getData(0)));
                count++;
                eventArrived = true;
            }
        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"IBM", 75.6f, 100});
        inputHandler.send(new Object[]{"WSO2", 75.6f, 100});
        Thread.sleep(500);
        Assert.assertEquals(2, count);
        Assert.assertEquals("Event arrived", true, eventArrived);
        siddhiManager.shutdown();

    }

    @Test
    public void testFilterQuery8() throws InterruptedException {
        log.info("Filter test8");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream(QueryFactory.createStreamDefinition().name("cseEventStream").attribute("symbol", Attribute.Type.STRING).attribute("price", Attribute.Type.FLOAT).attribute("volume", Attribute.Type.LONG));

        Query query = QueryFactory.createQuery();
        query.from(QueryFactory.inputStream("cseEventStream"));
        query.insertInto("OutputStream");
        query.project(
                QueryFactory.outputProjector().
                        project("symbol", Expression.variable("symbol")).
                        project("price", Expression.variable("price")).
                        project("increasedVolume", Expression.add(Expression.value(100), Expression.variable("volume"))).
                        project("decreasedVolume", Expression.minus(Expression.variable("volume"), Expression.value(50))).
                        project("multipliedVolume", Expression.multiply(Expression.value(4), Expression.variable("volume"))).
                        project("dividedVolume", Expression.divide(Expression.variable("volume"), Expression.value(8)))
        );


        String queryReference = siddhiManager.addQuery(query);

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertTrue("200".equals(inEvents[0].getData()[2].toString()));
                Assert.assertTrue("50".equals(inEvents[0].getData()[3].toString()));
                Assert.assertTrue("400".equals(inEvents[0].getData()[4].toString()));
                Assert.assertTrue("12.5".equals(inEvents[0].getData()[5].toString()));
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"WSO2", 55.6f, 100l});
        inputHandler.send(new Object[]{"IBM", 75.6f, 100l});
        inputHandler.send(new Object[]{"WSO2", 57.6f, 100l});
        Thread.sleep(500);

        siddhiManager.shutdown();

    }


    @Test
    public void testFilterQuery9() throws InterruptedException {
        log.info("Filter test9");

        SiddhiManager siddhiManager = new SiddhiManager();


        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream " +
                                                       "insert into OutputStream symbol, price, volume+100 as increasedVolume,(volume -50) as decreasedVolume,(4*volume) as multipliedVolume,(volume/8) as dividedVolume,(4*volume+34) as calculatedVolume " +
                                                       "group by symbol;");


        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertTrue("200".equals(inEvents[0].getData()[2].toString()));
                Assert.assertTrue("50".equals(inEvents[0].getData()[3].toString()));
                Assert.assertTrue("400".equals(inEvents[0].getData()[4].toString()));
                Assert.assertTrue("12.5".equals(inEvents[0].getData()[5].toString()));
                Assert.assertTrue("434".equals(inEvents[0].getData()[6].toString()));
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"WSO2", 55.6f, 100l});
        inputHandler.send(new Object[]{"IBM", 75.6f, 100l});
        inputHandler.send(new Object[]{"WSO2", 57.6f, 100l});
        Thread.sleep(500);

        siddhiManager.shutdown();

    }


    @Test(expected = MalformedAttributeException.class)
    public void testFilterQuery10() throws InterruptedException {
        log.info("Filter test10");

        SiddhiManager siddhiManager = new SiddhiManager();


        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream " +
                                                       "insert into OutputStream ssymbol, price, volume+100 as increasedVolume,(volume -50) as decreasedVolume,(4*volume) as multipliedVolume,(volume/8) as dividedVolume,(4*volume+34) as calculatedVolume " +
                                                       "group by symbol;");
        siddhiManager.shutdown();

    }

    @Test(expected = MalformedAttributeException.class)
    public void testFilterQuery11() throws InterruptedException {
        log.info("Filter test11");

        SiddhiManager siddhiManager = new SiddhiManager();


        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream[pric>200] " +
                                                       "insert into OutputStream symbol, price, volume+100 as increasedVolume,(volume -50) as decreasedVolume,(4*volume) as multipliedVolume,(volume/8) as dividedVolume,(4*volume+34) as calculatedVolume " +
                                                       "group by symbol;");
        siddhiManager.shutdown();

    }

    @Test(expected = SiddhiPraserException.class)
    public void testFilterQuery12() throws InterruptedException {
        log.info("Filter test12");

        SiddhiManager siddhiManager = new SiddhiManager();


        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream[pric>200] " +
                                                       "insert intutputStream symbol, price, volume+100 as increasedVolume,(volume -50) as decreasedVolume,(4*volume) as multipliedVolume,(volume/8) as dividedVolume,(4*volume+34) as calculatedVolume " +
                                                       "group by symbol;");

        siddhiManager.shutdown();

    }
    @Test
    public void testFilterQuery13() throws InterruptedException {
        log.info("Filter test13");

        SiddhiManager siddhiManager = new SiddhiManager();


        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream[symbol contains 'WS'] " +
                                                       "insert into OutputStream symbol, price, volume " +
                                                       ";");


        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count++;
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"WSO2", 55.6f, 100l});
        inputHandler.send(new Object[]{"IBM", 75.6f, 100l});
        inputHandler.send(new Object[]{"WSO2", 57.6f, 100l});
        Thread.sleep(500);
        Assert.assertEquals(2, count);
        siddhiManager.shutdown();

    }

    @Test (expected = OperationNotSupportedException.class)
    public void testFilterQuery14() throws InterruptedException {
        log.info("Filter test14");

        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");
        String queryReference = siddhiManager.addQuery("from cseEventStream[price contains 'WS'] " +
                                                       "insert into OutputStream symbol, price, volume " +
                                                       ";");
    }

    @Test
    public void testFilterQuery15() throws InterruptedException {
        log.info("Filter test15");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream[symbol instanceof string] " +
                                                       "insert into OutputStream symbol, price, volume " +
                                                       ";");

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count++;
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"WSO2", 55.6f, 100l});
        inputHandler.send(new Object[]{12, 75.6f, 100l});
        inputHandler.send(new Object[]{"WSO2", 57.6f, 100l});
        Thread.sleep(500);
        Assert.assertEquals(2, count);
        siddhiManager.shutdown();

    }

    @Test
    public void testFilterQuery16() throws InterruptedException {
        log.info("Filter test16");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream[price instanceof float] " +
                                                       "insert into OutputStream symbol, price, volume " +
                                                       ";");

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count++;
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"WSO2", 55.6f, 100l});
        inputHandler.send(new Object[]{"IBM", 75.6f, 100l});
        inputHandler.send(new Object[]{"WSO2", 57.6f, 100l});
        Thread.sleep(500);
        Assert.assertEquals(3, count);
        siddhiManager.shutdown();

    }

    @Test (expected = MalformedAttributeException.class)
    public void testFilterQuery17() throws InterruptedException {
        log.info("Filter test17");

        SiddhiManager siddhiManager = new SiddhiManager();
        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");
        String queryReference = siddhiManager.addQuery("from cseEventStream[price instanceof str] " +
                                                       "insert into OutputStream symbol, price, volume " +
                                                       ";");
    }

    @Test
    public void testFilterQuery18() throws InterruptedException {
        log.info("Filter test18");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream[symbol instanceof float] " +
                                                       "insert into OutputStream symbol, price, volume " +
                                                       ";");

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                count++;
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"WSO2", 55.6f, 100l});
        inputHandler.send(new Object[]{"IBM", 75.6f, 100l});
        inputHandler.send(new Object[]{"WSO2", 57.6f, 100l});
        Thread.sleep(500);
        Assert.assertEquals(0, count);
        siddhiManager.shutdown();

    }


    @Test
    public void testFilterQuery19() throws InterruptedException {
        log.info("Filter test19");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream[volume > 100] " +
                "insert into OutputStream symbol, price, volume " +
                ";");

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertEquals(103l, ((Long) inEvents[0].getData(2)).longValue());
                count++;
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"WSO2", 55.6f, 103l});
        inputHandler.send(new Object[]{"WSO2", 57.6f, 100l});
        Thread.sleep(100);
        Assert.assertEquals(1, count);
        siddhiManager.shutdown();

    }

    @Test
    public void testFilterQuery20() throws InterruptedException {
        log.info("Filter test20");

        SiddhiManager siddhiManager = new SiddhiManager();

        siddhiManager.defineStream("define stream cseEventStream (symbol string, price float, volume long) ");

        String queryReference = siddhiManager.addQuery("from cseEventStream[volume < 100] " +
                "insert into OutputStream symbol, price, volume " +
                ";");

        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                Assert.assertEquals(10l, ((Long) inEvents[0].getData(2)).longValue());
                count++;
            }

        });
        InputHandler inputHandler = siddhiManager.getInputHandler("cseEventStream");
        inputHandler.send(new Object[]{"WSO2", 55.6f, 103l});
        inputHandler.send(new Object[]{"WSO2", 57.6f, 10l});
        Thread.sleep(100);
        Assert.assertEquals(1, count);
        siddhiManager.shutdown();

    }
}
