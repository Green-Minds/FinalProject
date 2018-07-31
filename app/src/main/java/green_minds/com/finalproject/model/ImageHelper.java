package green_minds.com.finalproject.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

import com.parse.ParseFile;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageHelper {
    public static byte[] getByteArray(Bitmap b, int width, int quality){
        Bitmap bitmap = b;
        if(b.getWidth() > width){
            double scaleFactor = b.getWidth() / (width + 0.0);
            int newHeight = (int)(b.getHeight() / scaleFactor);
            bitmap =  Bitmap.createScaledBitmap(b, width, newHeight, false);
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static Bitmap maxSizeBitmap(Bitmap b, int width){
        if(b.getWidth() > width){
            double scaleFactor = b.getWidth() / (width + 0.0);
            int newHeight = (int)(b.getHeight() / scaleFactor);
            return Bitmap.createScaledBitmap(b, width, newHeight, false);
        }
        return b;
    }

    public static ParseFile getSmallerParseFile(Bitmap b){
        return new ParseFile(getSmallerFileName(), getByteArray(b, 250, 50));
    }

    public static ParseFile getParseFile(Bitmap b){
        return new ParseFile(getFileName(), getByteArray(b, b.getWidth(), 100));
    }

    public static String getFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        return "IMG_" + timeStamp + ".jpg";
    }

    public static String getSmallerFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        return "IMG_SMALLER_" + timeStamp + ".jpg";
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

}
