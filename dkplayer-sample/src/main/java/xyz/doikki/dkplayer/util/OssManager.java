package xyz.doikki.dkplayer.util;

import android.content.Context;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;

public class OssManager {
    /**
     * Context
     */
    private Context mContext;
    /**
     * bucket name
     */
    private String mBucketName;
    /**
     * access key id
     */
    private String mAccessKeyId;
    /**
     * access key secret
     */
    private String mAccessKeySecret;
    /**
     * end point url
     */
    private String mEndPoint;
    /**
     * file name or file dir
     */
    private String mObjectKey;
    /**
     * local file path
     */
    private String mLocalFilePath;
    /**
     * push file progress listener
     */
    private OnPushProgressListener onPushProgressListener;
    /**
     * push file state
     */
    private OnPushStateListener onPushStateListener;
    /**
     * OSS async task
     */
    private OSSAsyncTask mOSSAsyncTask;

    private OssManager(Context context, String bucketName, String accessKeyId, String accessKeySecret, String endPoint, String objectKey, String localFilePath) {
        this.mContext = context;
        this.mBucketName = bucketName;
        this.mAccessKeyId = accessKeyId;
        this.mAccessKeySecret = accessKeySecret;
        this.mEndPoint = endPoint;
        this.mObjectKey = objectKey;
        this.mLocalFilePath = localFilePath;
    }

    /**
     * set push file progress listener,pushing call back onProgress(PutObjectRequest request, long currentSize, long totalSize)
     *
     * @param listener push file progress listener
     */
    public void setPushProgressListener(OnPushProgressListener listener) {
        this.onPushProgressListener = listener;
    }

    /**
     * set push file state listener,push success call back onSuccess(PutObjectRequest request, PutObjectResult result)
     * push failed call back onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException)
     *
     * @param listener push file state listener
     */
    public void setPushStateListener(OnPushStateListener listener) {
        this.onPushStateListener = listener;
    }

    /**
     * push file to oss,this method is async task
     */
    public void push() {
        OSSCredentialProvider ossCredentialProvider = new OSSPlainTextAKSKCredentialProvider(mAccessKeyId, mAccessKeySecret);
        OSS oss = new OSSClient(mContext.getApplicationContext(), mEndPoint, ossCredentialProvider);
        onPush(oss);
    }

    /**
     * push file to oss,this method is async task
     */
    public void push(String accessKeyId, String accessKeySecret, String securityToken) {
        if (accessKeyId == null || accessKeySecret == null || securityToken == null) return;
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
        OSS oss = new OSSClient(mContext.getApplicationContext(), mEndPoint, credentialProvider);
        onPush(oss);
    }

    /**
     * push
     * @param oss OSS
     */
    private void onPush(OSS oss) {
        PutObjectRequest put = new PutObjectRequest(mBucketName, mObjectKey, mLocalFilePath);
        // 异步上传时可以设置进度回调。
        put.setProgressCallback((request, currentSize, totalSize) -> {
            // LogUtils.d("currentSize = " + currentSize, "totalSize = " + totalSize);
            if (onPushProgressListener != null) {
                onPushProgressListener.onProgress(request, currentSize, totalSize);
            }
        });

        mOSSAsyncTask = oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
            @Override
            public void onSuccess(PutObjectRequest request, PutObjectResult result) {
//                LogUtils.d("PutObject", "UploadSuccess");
//                LogUtils.d("ETag", result.getETag());
//                LogUtils.d("RequestId", result.getRequestId());
                if (onPushStateListener != null) {
                    onPushStateListener.onSuccess(request, result);
                }
            }

            @Override
            public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                // 请求异常。
                if (clientException != null) {
                    // 本地异常，如网络异常等。
                    // LogUtils.e(clientException.getMessage());
                    System.out.println(clientException.getMessage());
                }
                if (serviceException != null) {
                    // 服务异常。
//                    LogUtils.e("ErrorCode", serviceException.getErrorCode());
//                    LogUtils.e("RequestId", serviceException.getRequestId());
//                    LogUtils.e("HostId", serviceException.getHostId());
//                    LogUtils.e("RawMessage", serviceException.getRawMessage());
                    System.out.println("ErrorCode" +  serviceException.getErrorCode());
                }

                if (onPushStateListener != null) {
                    onPushStateListener.onFailure(request, clientException, serviceException);
                }
            }
        });
    }

    /**
     * cancel file push task
     */
    public void cancelPush() {
        if (mOSSAsyncTask != null && !mOSSAsyncTask.isCanceled() && !mOSSAsyncTask.isCompleted()) {
            mOSSAsyncTask.cancel();
        }
    }

    /**
     * OssManager builder,init params
     */
    public static class Builder {

        private Context context;
        private String bucketName;
        private String accessKeyId;
        private String accessKeySecret;
        private String endPoint;
        private String objectKey;
        private String localFilePath;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder bucketName(String bucketName) {
            this.bucketName = bucketName;
            return this;
        }

        public Builder accessKeyId(String accessKeyId) {
            this.accessKeyId = accessKeyId;
            return this;
        }

        public Builder accessKeySecret(String accessKeySecret) {
            this.accessKeySecret = accessKeySecret;
            return this;
        }

        public Builder endPoint(String endPint) {
            this.endPoint = endPint;
            return this;
        }

        public Builder objectKey(String objectKey) {
            this.objectKey = objectKey;
            return this;
        }

        public Builder localFilePath(String localFilePath) {
            this.localFilePath = localFilePath;
            return this;
        }

        public OssManager build() {
            return new OssManager(context, bucketName, accessKeyId, accessKeySecret, endPoint, objectKey, localFilePath);
        }
    }

    public interface OnPushProgressListener {
        void onProgress(PutObjectRequest request, long currentSize, long totalSize);
    }

    public interface OnPushStateListener {
        void onSuccess(PutObjectRequest request, PutObjectResult result);

        void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException);
    }

}

