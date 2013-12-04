package org.wso2.carbon.cep.core.distributing;


import org.apache.axiom.om.OMElement;
import org.wso2.carbon.cep.core.Bucket;
import org.wso2.carbon.cep.core.exception.CEPConfigurationException;
import org.wso2.carbon.cep.core.internal.config.BucketHelper;

public class Util {

  public static Bucket getBucket(OMElement omElement) throws CEPConfigurationException {
     return BucketHelper.fromOM(omElement);
  }

  public  static OMElement getOM(Bucket bucket){
      return  BucketHelper.bucketToOM(bucket);
  }



}
