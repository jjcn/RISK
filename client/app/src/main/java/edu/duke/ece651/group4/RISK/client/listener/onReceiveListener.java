package edu.duke.ece651.group4.RISK.client.listener;

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
     * @param errMsg error message_menu
     */
    void onFailure(String errMsg);
}

