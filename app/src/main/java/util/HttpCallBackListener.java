package util;

/**
 * Created by Jason on 2016/3/13.
 */
public interface HttpCallBackListener {
    /*
    * 帮助接口，用于扩展请求后回调
    * */
    void onFinish(String response);
    void onError(Exception e);
}
