package xyz.doikki.dkplayer.util;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSConstants;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.HmacSHA1Signature;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSCustomSignerCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSFederationToken;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.google.android.gms.common.util.Base64Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import xyz.doikki.dkplayer.BuildConfig;

public class AliOssUtils {

    static AliOssUtils instance;
    private String BUCKETNAME = "zfshop-img",/**/
            ENDPOINT = "https://oss-cn-hangzhou.aliyuncs.com" ,/*阿里云请求域名*/
            C_NAME = "http://xxx.xxx.com",/*绑定的请求域名*/
            PREFIX = (BuildConfig.DEBUG?"dev":"zfshop")/*文件存放目录*/;
    OSS oss;
    public static AliOssUtils getInstance() {
        if(instance == null){
            instance = new AliOssUtils();
        }
        return instance;
    }

    /**
     * 初始化
     * @param context
     */
    public void init(Context context){
        // 配置类如果不设置，会有默认配置。
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒。
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒。
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个。
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次。
        OSSLog.enableLog();  //调用此方法即可开启日志,
        initOss(context,conf);
        //initThreadOss(context,conf);
    }

    /**
     * 初始化oss 及时更新
     * @param context
     * @param conf
     */
    private void initOss(Context context,ClientConfiguration conf){
        // 推荐使用OSSAuthCredentialsProvider。token过期可以及时更新。
        OSSCredentialProvider credentialProvider =  createSTSProvider();
        oss = new OSSClient(context, C_NAME, credentialProvider,conf);
    }

    /**
     * 初始化oss  手动更新
     * @param context
     * @param conf
     */
    private void initThreadOss(Context context,ClientConfiguration conf){
        new Thread(){
            @Override
            public void run() {
                try {
                    JSONObject stsToken = getStsToken();
                    OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(stsToken.getString("accessKeyId"), stsToken.getString("accessKeySecret"), stsToken.getString("securityToken"));
                    //oss.updateCredentialProvider(new OSSStsTokenCredentialProvider("<StsToken.AccessKeyId>", "<StsToken.SecretKeyId>", "<StsToken.SecurityToken>")); //需要手动更新token
                    oss = new OSSClient(context, C_NAME, credentialProvider,conf);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 请求sts 鉴权token
     * @return
     */
    private JSONObject getStsToken(){
        JSONObject stsObject = null;
        try {
            URL stsUrl = new URL(ENDPOINT+"system/oss/getSts");
            HttpURLConnection conn = (HttpURLConnection) stsUrl.openConnection();
            // 请求方式
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");//json格式// 连接
            conn.connect();
            InputStream input = conn.getInputStream();
            String jsonText = IOUtils.readStreamAsString(input, OSSConstants.DEFAULT_CHARSET_NAME);
            JSONObject jsonObjs = new JSONObject(jsonText);
            Log.e("credentialProvider", "jsonText:"+jsonText);
            int code = jsonObjs.getInt("code");
            if(code == 200){
                String sts = jsonObjs.getString("sts");
                sts = Arrays.toString(Base64Utils.decode(sts));
                Log.e("credentialProvider", "sts:"+sts);
                stsObject = new JSONObject(sts);
                BUCKETNAME = jsonObjs.getString("bucket");
                PREFIX = jsonObjs.getString("prefix");
                C_NAME = jsonObjs.getString("url");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stsObject;
    }

    /**
     * sts 鉴权  推荐使用OSSAuthCredentialsProvider。token过期可以及时更新。
     * @return
     */
    private OSSCredentialProvider createSTSProvider(){
        OSSCredentialProvider credentialProvider = new OSSFederationCredentialProvider() {
            @Override
            public OSSFederationToken getFederationToken() {
                try {
                    JSONObject jsonObject = getStsToken();
                    String ak = jsonObject.getString("accessKeyId");
                    String sk = jsonObject.getString("accessKeySecret");
                    String token = jsonObject.getString("securityToken");
                    String expiration = jsonObject.getString("expiration");
                    return new OSSFederationToken(ak, sk, token, expiration);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        return credentialProvider;
    }

    /**
     * 加签鉴权
     * @return
     */
    private OSSCredentialProvider createSignProvider(){
        OSSCredentialProvider credentialProvider = new OSSCustomSignerCredentialProvider() {
            @Override
            public String signContent(String content) {
                // 按照OSS规定的签名算法加签字符串，并将得到的加签字符串拼接AccessKeyId后返回。
                // 将加签的字符串传给您的服务器，然后返回签名。如果因某种原因加签失败，服务器描述错误信息后返回null。
                return "OSS LTAI4GFTiKiBKj33yVGYECXf:" + new HmacSHA1Signature().computeSignature("7ki1ITVXMIFaDfAM1DdrvHJtvGp6zD", content).trim();
            }
        };
        return credentialProvider;
    }

    /**
     * 多文件上传
     * @param listener
     * @param paths
     */
    public void uploadFiles(MultiUploadFileListener listener,String... paths){// 构造上传请求。
        // 构造上传请求。
        final List<String> fileStrings = new ArrayList<>(Arrays.asList(paths));
        uploadFile(new UploadFileListener() {
            @Override
            public void uploadProgress(String path, long currentSize, long totalSize) {
                listener.uploadProgress(path,currentSize,totalSize);
            }

            @Override
            public void uploadSuccess(String url) {
                fileStrings.remove(0);//清除第一位
                fileStrings.add(url);//追加上传后的url
                if(fileStrings.get(0).indexOf(C_NAME)>-1){//判断第一个是否包含域名 确定所有文件都上传完毕
                    listener.uploadSuccess(fileStrings);
                }else{
                    uploadFiles(listener,fileStrings.toArray(new String[fileStrings.size()]));
                }
            }

            @Override
            public void uploadFailed(String message) {
                listener.uploadFailed(message);
            }
        },paths[0]);
        // task.cancel(); // 可以取消任务。
        // task.waitUntilFinished(); // 等待任务完成。
    }

    /**
     * 单个文件上传
     * @param listener
     * @param path
     */
    public void uploadFile(UploadFileListener listener,String path){// 构造上传请求。
        // 构造上传请求。
        final String fileName = PREFIX+ File.separator + UUID.randomUUID().toString();
        PutObjectRequest put = new PutObjectRequest(BUCKETNAME,fileName, path);

        // 异步上传时可以设置进度回调。
        put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
            @Override
            public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
                listener.uploadProgress(request.getUploadFilePath(),currentSize,totalSize);
            }
        });

        OSSAsyncTask task = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                String url = C_NAME+"/"+fileName;
                listener.uploadSuccess(url);
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                listener.uploadFailed("上传失败");
                // 请求异常。
                if (clientExcepion != null) {
                    // 本地异常，如网络异常等。
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常。
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        // task.cancel(); // 可以取消任务。
        // task.waitUntilFinished(); // 等待任务完成。
    }
    public interface UploadFileListener{
        void uploadProgress(String path,long currentSize, long totalSize);

        void uploadSuccess(String url);

        void uploadFailed(String message);
    }
    public interface MultiUploadFileListener{
        void uploadProgress(String path,long currentSize, long totalSize);

        void uploadSuccess(List<String> urls);

        void uploadFailed(String message);
    }
}

