package org.wso2.carbon.cep.core.decomposer;

import org.wso2.carbon.cep.core.Query;

/**
 * Created with IntelliJ IDEA.
 * User: sachini
 * Date: 6/13/13
 * Time: 10:56 AM
 * To change this template use File | Settings | File Templates.
 */
public interface QueryDecomposer {


    /**
    * This method is used to decompose a complex query into small sub-queries
    * @param query - query to be decomposed
    * */
    public void decompose(Query query) ;

}
