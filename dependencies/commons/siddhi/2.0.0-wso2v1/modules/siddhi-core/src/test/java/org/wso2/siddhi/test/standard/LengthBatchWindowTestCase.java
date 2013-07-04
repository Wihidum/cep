package org.wso2.siddhi.test.standard;


import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.config.SiddhiConfiguration;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.query.output.QueryCallback;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.util.EventPrinter;

public class LengthBatchWindowTestCase {

    static final Logger log = Logger.getLogger(LengthBatchWindowTestCase.class);

    private int count;
    private boolean eventArrived;
    private boolean inEventBatchSize;
    private boolean removeEventBatchSize;
    SiddhiConfiguration configuration;
    SiddhiManager siddhiManager;

    @Before
    public void initialize() {
        count = 0;
        eventArrived = false;
        inEventBatchSize = false;
        removeEventBatchSize = false;
        configuration = new SiddhiConfiguration();
        configuration.setAsyncProcessing(false);
        siddhiManager = new SiddhiManager(configuration);
    }


    @Test
    public void testWindowQuery() throws InterruptedException {
        log.info("Window length Test Running");
        siddhiManager.defineStream("define stream purchase (cardNo string, price float) ");
        String queryReference = siddhiManager.addQuery("from purchase[price >= 30]#window.lengthBatch(4)" +
                                                       "insert  all-events  into PotentialFraud cardNo, price ");
        siddhiManager.addCallback(queryReference, new QueryCallback() {

            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                eventArrived = true;
                count++;
                if (inEvents != null) {
                    if (inEvents.length == 4) {
                        inEventBatchSize = true;
                    }  else {
                        Assert.fail("Length has to be 4 but it was " + inEvents.length);
                    }
                }
                if (removeEvents != null) {
                    if (removeEvents.length == 4) {
                        removeEventBatchSize = true;
                    } else {
                        Assert.fail("Length has to be 4 but it was " + removeEvents.length);
                    }
                }
            }

        });
        InputHandler loginSucceedEvents = siddhiManager.getInputHandler("purchase");

        for (int i = 0; i < 4; i++) {
            loginSucceedEvents.send(new Object[]{"3234-3244-2432-4124", 73.36f});
            loginSucceedEvents.send(new Object[]{"1234-3244-2432-123", 26.36f});
            loginSucceedEvents.send(new Object[]{"5768-3244-2432-5646", 28.36f});
            loginSucceedEvents.send(new Object[]{"9853-3244-2432-4125", 78.36f});
        }
        Thread.sleep(1000);
        Assert.assertEquals("Event arrived", true, eventArrived);
        Assert.assertEquals("Event count", 3, count);
        Assert.assertEquals("InEvent Batch Size Passed", true, inEventBatchSize);
        Assert.assertEquals("RemoveEvent Batch Size Passed", true, removeEventBatchSize);
        siddhiManager.shutdown();


    }

    @Test
    public void testWindowAvgerageQuery() throws InterruptedException {
        log.info("Window length Test Average Testing Running");


        siddhiManager.defineStream("define stream purchase (cardNo string, price float) ");

        String queryReference = siddhiManager.addQuery("from purchase  #window.lengthBatch(2)" +
                                                       "insert  all-events  into PotentialFraud  avg(price) as avgPrice");

        siddhiManager.addCallback(queryReference, new QueryCallback() {

            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);
                eventArrived = true;
                count++;
                if (inEvents != null) {
                    if (inEvents.length == 1) {
                        Assert.assertEquals(150.0, inEvents[0].getData()[0]);
                    } else {
                        Assert.fail("Length has to be 1 but it was " + inEvents.length);
                    }
                }
                if (removeEvents != null) {
                    if (removeEvents.length == 1) {
                        Assert.assertEquals(0, removeEvents[0].getData()[0]);
                    } else {
                        Assert.fail("Length has to be 1 but it was " + removeEvents.length);
                    }
                }
            }

        });
        InputHandler loginSucceedEvents = siddhiManager.getInputHandler("purchase");

        for (int i = 0; i < 3; i++) {
            loginSucceedEvents.send(new Object[]{"3234-3244-2432-4124", 100.00f});
            loginSucceedEvents.send(new Object[]{"1234-3244-2432-123", 200.00f});

        }
        Thread.sleep(1000);
        Assert.assertEquals("Event arrived", true, eventArrived);
        Assert.assertEquals("Event count", 5, count);

        siddhiManager.shutdown();


    }

    @Test()
    public void testWindowSumQuery() throws InterruptedException {
        log.info("Window length Test Sum Testing Running");


        siddhiManager.defineStream("define stream purchase (cardNo string, price float) ");

        String queryReference = siddhiManager.addQuery("from purchase  #window.lengthBatch(2)" +
                                                       "insert  all-events  into PotentialFraud  sum(price) as avgPrice");
        siddhiManager.addCallback(queryReference, new QueryCallback() {

            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);

                eventArrived = true;
                count++;
                if (inEvents != null) {
                    if (inEvents.length == 1) {
                        Assert.assertEquals(200.0, inEvents[0].getData()[0]);
                    } else {
                        Assert.fail("Length has to be 1 but it was " + inEvents.length);
                    }
                }
                if (removeEvents != null) {
                    if (removeEvents.length == 1) {
                        Assert.assertEquals(0.0, removeEvents[0].getData()[0]);
                    } else {
                        Assert.fail("Length has to be 1 but it was " + removeEvents.length);
                    }
                }
            }

        });
        InputHandler loginSucceedEvents = siddhiManager.getInputHandler("purchase");
        for (int i = 0; i < 3; i++) {
            loginSucceedEvents.send(new Object[]{"3234-3244-2432-4124", 100.00f});
            loginSucceedEvents.send(new Object[]{"1234-3244-2432-123", 100.00f});
        }
        Thread.sleep(10000);
        Assert.assertEquals("Event arrived", true, eventArrived);
        Assert.assertEquals("Event count", 5, count);

        siddhiManager.shutdown();


    }

    @Test()
    public void testWindowMaxQuery() throws InterruptedException {
        log.info("Window length Test Sum Testing Running");
        siddhiManager.defineStream("define stream purchase (cardNo string, price float) ");
        String queryReference = siddhiManager.addQuery("from purchase  #window.lengthBatch(2)" +
                                                       "insert  all-events  into PotentialFraud  Max(price) as avgPrice, price");
        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);

                eventArrived = true;
                count++;
                if (inEvents != null) {
                    if (inEvents.length == 1) {
                        Assert.assertEquals(130.0f, inEvents[0].getData()[0]);
                    } else {
                        Assert.fail("Length has to be 1 but it was " + inEvents.length);
                    }
                }

            }

        });
        InputHandler loginSucceedEvents = siddhiManager.getInputHandler("purchase");
        for (int i = 0; i < 5; i++) {
            loginSucceedEvents.send(new Object[]{"3234-3244-2432-4124", 130.00f});
            loginSucceedEvents.send(new Object[]{"1234-3244-2432-123", 100.00f});
        }
        Thread.sleep(1000);
        Assert.assertEquals("Event arrived", true, eventArrived);
        Assert.assertEquals("Event count", 9, count);
        siddhiManager.shutdown();
    }

    @Test()
    public void testWindowCountQuery() throws InterruptedException {
        log.info("Window length Test Sum Testing Running");
        siddhiManager.defineStream("define stream purchase (cardNo string, price float) ");
        String queryReference = siddhiManager.addQuery("from purchase  #window.lengthBatch(4)" +
                                                       "insert  all-events  into PotentialFraud  count(price) as avgPrice, price");
        siddhiManager.addCallback(queryReference, new QueryCallback() {
            @Override
            public void receive(long timeStamp, Event[] inEvents, Event[] removeEvents) {
                EventPrinter.print(timeStamp, inEvents, removeEvents);

                eventArrived = true;
                count++;
                if (inEvents != null) {
                    if (inEvents.length == 1) {
                        Assert.assertEquals(4l, inEvents[0].getData()[0]);
                    } else {
                        Assert.fail("Length has to be 1 but it was " + inEvents.length);
                    }
                }
                if (removeEvents != null) {
                    if (removeEvents.length == 1) {
                        Assert.assertEquals(0l, removeEvents[0].getData()[0]);
                    } else {
                        Assert.fail("Length has to be 1 but it was " + removeEvents.length);
                    }
                }

            }

        });
        InputHandler loginSucceedEvents = siddhiManager.getInputHandler("purchase");
        for (int i = 0; i < 5; i++) {
            loginSucceedEvents.send(new Object[]{"3234-3244-2432-4124", 130.00f});
            loginSucceedEvents.send(new Object[]{"1234-3244-2432-123", 100.00f});
        }
        Thread.sleep(1000);
        Assert.assertEquals("Event arrived", true, eventArrived);
        Assert.assertEquals("Event count", 3, count);
        siddhiManager.shutdown();
    }

}

