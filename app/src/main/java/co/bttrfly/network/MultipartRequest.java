package co.bttrfly.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by jiho on 1/12/15.
 */
public abstract class MultipartRequest extends Request<JSONObject> {

    private final Response.Listener<JSONObject> mListener;
    public final ProgressReporter mProgressReporter;
    private HttpEntity httpEntity = null;
    private long mFileLength;


    public MultipartRequest(
            String url,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener,
            ProgressReporter progressReporter,
            long fileLength) {
        super(Method.POST, url, errorListener);
        mFileLength = fileLength;
        mListener = listener;
        mProgressReporter = progressReporter;


    }

    protected abstract HttpEntity createHttpEntity();

    private HttpEntity getHttpEntity() {
        if (httpEntity == null) {
            httpEntity = createHttpEntity();
        }


        return httpEntity;
    }

    @Override
    public String getBodyContentType() {
        return getHttpEntity().getContentType().getValue();
    }


    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }


    @Override
    protected void deliverResponse(JSONObject jsonObject) {
        if (mListener != null) {
            mListener.onResponse(jsonObject);
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            getHttpEntity().writeTo(new CountingOutputStream(bos, mFileLength,
                    mProgressReporter));
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    public static interface ProgressReporter {
        void transferred(long transferredBytes, int progress);
    }

    public static class CountingOutputStream extends FilterOutputStream {
        private final ProgressReporter progListener;
        private long transferred;
        private long fileLength;

        public CountingOutputStream(final OutputStream out, long fileLength,
                                    final ProgressReporter listener) {
            super(out);
            this.fileLength = fileLength;
            this.progListener = listener;
            this.transferred = 0;
        }

        public void write(byte[] b, int off, int len) throws IOException {
            out.write(b, off, len);
            if (progListener != null) {
                this.transferred += len;
                int prog = (int) (transferred * 100 / fileLength);
                this.progListener.transferred(this.transferred, prog);
            }
        }

        public void write(int b) throws IOException {
            out.write(b);
            if (progListener != null) {
                this.transferred++;
                int prog = (int) (transferred * 100 / fileLength);
                this.progListener.transferred(this.transferred, prog);
            }
        }
    }
}
