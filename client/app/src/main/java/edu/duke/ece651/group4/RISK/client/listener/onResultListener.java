package edu.duke.ece651.group4.RISK.client.listener;

/**
 * check if successfully send Object to remote server.
 */
public interface onResultListener {
    /**
     * remote server received sent object
     */
    void onSuccess();

    /**
     *
     * @param errMsg error message
     */
    void onFailure(String errMsg);
}
