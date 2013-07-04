/*
*  Copyright (c) 2005-2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package org.wso2.carbon.cep.core.internal.persistance;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.internal.config.BucketHelper;
import org.wso2.carbon.utils.xml.XMLPrettyPrinter;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class CEPResourcePersister {
    private static final Log log = LogFactory.getLog(CEPResourcePersister.class);
    private static BlockingQueue<String> allowPersistent = new LinkedBlockingDeque<String>();
    private static BlockingQueue<String> disallowPersistent = new LinkedBlockingDeque<String>();

    public static boolean save(String bucket, String bucketName,
                               String bucketPath) {
        try {
            if (!allowPersistent.remove(bucketPath)) {
                return false;
            }
            /* save contents to .xml file */
            XMLPrettyPrinter xmlPrettyPrinter = new XMLPrettyPrinter(new ByteArrayInputStream(bucket.getBytes()));
            BufferedWriter out = new BufferedWriter(new FileWriter(bucketPath));
            out.write(xmlPrettyPrinter.xmlFormat());
            out.close();
        } catch (IOException e) {
            log.error("Error while saving " + bucketName, e);
        }
        return true;
    }

    public static boolean delete(String bucketPath) {
        if (!disallowPersistent.contains(bucketPath)) {
            return false;
        }
        File file = new File(bucketPath);
        if (file.exists()) {
            boolean fileDeleted = file.delete();
            if (!fileDeleted) {
                log.error("Could not delete " + bucketPath);
            }
        }
        return true;
    }

    public static boolean save(Bucket bucket, String bucketPath) {
        return save(BucketHelper.bucketToOM(bucket).toString(), bucket.getName(), bucketPath);
    }

    public static void allowPersistent(String bucketPath) {
        try {
            allowPersistent.put(bucketPath);
        } catch (InterruptedException e) {
            log.error("Error while allowing persistence for " + bucketPath, e);
        }
    }

    public static void clearAllowPersistent(String bucketPath) {
        allowPersistent.remove(bucketPath);
    }

    public static void disallowPersistent(String bucketPath) {
        try {
            disallowPersistent.put(bucketPath);
        } catch (InterruptedException e) {
            log.error("Error while allowing persistence for " + bucketPath, e);
        }
    }

    public static void clearDisallowPersistent(String bucketPath) {
        disallowPersistent.remove(bucketPath);
    }


}
