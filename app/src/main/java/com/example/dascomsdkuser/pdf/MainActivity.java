package com.example.dascomsdkuser.pdf;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.github.barteksc.pdfviewer.PDFView;
import com.shockwave.pdfium.PdfDocument;
import com.shockwave.pdfium.PdfiumCore;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import static android.os.ParcelFileDescriptor.MODE_READ_WRITE;


public class MainActivity extends AppCompatActivity {
    ParcelFileDescriptor fd;
    PDFView pdfView;
    String path;
    String TAG = "Main";
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        pdfView = (PDFView) findViewById(R.id.pdfView);
        imageView = (ImageView) findViewById(R.id.imageView);


    }

    public void  file(View view ){
        Intent intent = new Intent(this,FileActivity.class);
        startActivityForResult(intent,1);
    }

    public void button(View view){

//        pdfView.fromFile(new File(path))
//                .enableSwipe(true)
//                .swipeHorizontal(false)
//                .enableDoubletap(true)
//                .defaultPage(0)
//                .load();

        openPdf();
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        switch (requestCode){
            case 0:

                break;
            case 1:
                if (resultCode==20){
                    Log.d("", "onActivityResult: ");
                     path = data.getExtras().getString("path");
                    Log.d("TAG", "onActivityResult: "+path);
//                    try {
//                        fd.open(new File(path),MODE_READ_WRITE);
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }
                    Log.d("TAG", "onActivityResult: file"+new File(path));
                    Log.d("TAG", "onActivityResult: fd"+fd);
                }
                break;
        }
        super.onActivityResult(requestCode,resultCode,data);
    }


    void openPdf() {

        try {
            fd =  ParcelFileDescriptor.open(new File(path),MODE_READ_WRITE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (fd==null){
            Log.d(TAG, "openPdf: "+new File(path));
            Log.d(TAG, "openPdf: "+fd);
            return;
        }
        int pageNum = 0;
        PdfiumCore pdfiumCore = new PdfiumCore(this);
        try {

            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            Log.d(TAG, "openPdf: "+pdfiumCore.getPageCount(pdfDocument));
            pdfiumCore.openPage(pdfDocument, pageNum);

            int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNum);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNum);

            // ARGB_8888 - best quality, high memory usage, higher possibility of OutOfMemoryError
            // RGB_565 - little worse quality, twice less memory usage
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bitmap, pageNum, 0, 0,
                    width*2, height*2,true);
            //if you need to render annotations and form fields, you can use
            //the same method above adding 'true' as last param

            imageView.setImageBitmap(bitmap);

            printInfo(pdfiumCore, pdfDocument);

            pdfiumCore.closeDocument(pdfDocument); // important!
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void printInfo(PdfiumCore core, PdfDocument doc) {
        PdfDocument.Meta meta = core.getDocumentMeta(doc);
        Log.e(TAG, "title = " + meta.getTitle());
        Log.e(TAG, "author = " + meta.getAuthor());
        Log.e(TAG, "subject = " + meta.getSubject());
        Log.e(TAG, "keywords = " + meta.getKeywords());
        Log.e(TAG, "creator = " + meta.getCreator());
        Log.e(TAG, "producer = " + meta.getProducer());
        Log.e(TAG, "creationDate = " + meta.getCreationDate());
        Log.e(TAG, "modDate = " + meta.getModDate());

        printBookmarksTree(core.getTableOfContents(doc), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            Log.e(TAG, String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

}
