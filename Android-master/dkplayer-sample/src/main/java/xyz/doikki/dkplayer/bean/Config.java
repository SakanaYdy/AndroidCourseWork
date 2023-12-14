package xyz.doikki.dkplayer.bean;

public class Config {

    // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
    public static final String OSS_ENDPOINT = "https://ydy-sky.oss-cn-beijing.aliyuncs.com";
    // 填写callback地址。
    public static final String OSS_CALLBACK_URL = "https://oss-demo.aliyuncs.com:23450";
    // 填写STS鉴权服务器地址。
    // 您还可以根据工程sts_local_server目录中本地鉴权服务脚本代码启动本地STS鉴权服务器。
    public static final String STS_SERVER_URL = "http://****/sts/getsts";
    
    public static final String BUCKET_NAME = "ydy-sky";
    public static final String OSS_ACCESS_KEY_ID = "LTAI5tF8MSLykjbDwiU5RRJx";;
    public static final String OSS_ACCESS_KEY_SECRET = "BWUWiScZkgcx74HSHr7gwztFJsM242";

    public static final int DOWNLOAD_SUC = 1;
    public static final int DOWNLOAD_Fail = 2;
    public static final int UPLOAD_SUC = 3;
    public static final int UPLOAD_Fail = 4;
    public static final int UPLOAD_PROGRESS = 5;
    public static final int LIST_SUC = 6;
    public static final int HEAD_SUC = 7;
    public static final int RESUMABLE_SUC = 8;
    public static final int SIGN_SUC = 9;
    public static final int BUCKET_SUC = 10;
    public static final int GET_STS_SUC = 11;
    public static final int MULTIPART_SUC = 12;
    public static final int STS_TOKEN_SUC = 13;
    public static final int FAIL = 9999;
    public static final int REQUESTCODE_AUTH = 10111;
    public static final int REQUESTCODE_LOCALPHOTOS = 10112;
}