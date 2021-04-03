package edu.duke.ece651.group4.RISK.client.activity;

/**
 * Receive return objects from remote server.
 */
public interface onReceiveListener {
    /**
     *
     * @param o the received object
     */
    void onSuccess(Object o);

    /**
     * @param errMesg error message
     */
    void onFailure(String errMsg);
}
