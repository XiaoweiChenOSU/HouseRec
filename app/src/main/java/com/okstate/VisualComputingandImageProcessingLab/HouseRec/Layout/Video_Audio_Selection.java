package com.okstate.VisualComputingandImageProcessingLab.HouseRec.Layout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.downloader.PRDownloader;
import com.okstate.VisualComputingandImageProcessingLab.HouseRec.R;

import org.jetbrains.annotations.Nullable;

import kotlin.jvm.internal.Intrinsics;

public class Video_Audio_Selection extends AppCompatActivity {


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.video_selection_maybe);
       // PRDownloader.initialize(this.getApplicationContext());
        //Intent var10001 = this.getIntent();
      //  Intrinsics.checkExpressionValueIsNotNull(var10001, "intent");
      //  this.checkPdfAction(var10001);
        selectPdfFromStorage();
    }

    private final void selectPdfFromStorage() {
        Toast.makeText((Context)this, (CharSequence)"Select Video", Toast.LENGTH_LONG).show();
        Intent browseStorage = new Intent("android.intent.action.GET_CONTENT");
        browseStorage.setType("application/mp4");
        browseStorage.addCategory("android.intent.category.OPENABLE");
        this.startActivityForResult(Intent.createChooser(browseStorage, (CharSequence)"Select PDF"), 99);
    }

}
/*

package com.mindorks.example.openpdffile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.mindorks.example.openpdffile.R.id;
import com.mindorks.example.openpdffile.utils.FileUtils;
import java.io.File;
import java.util.HashMap;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 18},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u001d2\u00020\u0001:\u0001\u001dB\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0002J \u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0006\u0010\u000b\u001a\u00020\tH\u0002J\"\u0010\f\u001a\u00020\u00042\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0006H\u0014J\u0012\u0010\u0011\u001a\u00020\u00042\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0014J\b\u0010\u0014\u001a\u00020\u0004H\u0002J\u0010\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00020\tH\u0002J\u0010\u0010\u0017\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J\u0012\u0010\u001a\u001a\u00020\u00042\b\u0010\u001b\u001a\u0004\u0018\u00010\u001cH\u0002¨\u0006\u001e"},
   d2 = {"Lcom/mindorks/example/openpdffile/PdfViewActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "checkPdfAction", "", "intent", "Landroid/content/Intent;", "downloadPdfFromInternet", "url", "", "dirPath", "fileName", "onActivityResult", "requestCode", "", "resultCode", "data", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "selectPdfFromStorage", "showPdfFromAssets", "pdfName", "showPdfFromFile", "file", "Ljava/io/File;", "showPdfFromUri", "uri", "Landroid/net/Uri;", "Companion", "app_debug"}
)
public final class PdfViewActivity extends AppCompatActivity {
   private static final int PDF_SELECTION_CODE = 99;
   @NotNull
   public static final PdfViewActivity.Companion Companion = new PdfViewActivity.Companion((DefaultConstructorMarker)null);
   private HashMap _$_findViewCache;

   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      this.setContentView(1300010);
      PRDownloader.initialize(this.getApplicationContext());
      Intent var10001 = this.getIntent();
      Intrinsics.checkExpressionValueIsNotNull(var10001, "intent");
      this.checkPdfAction(var10001);
   }

   private final void showPdfFromAssets(String pdfName) {
      ((PDFView)this._$_findCachedViewById(id.pdfView)).fromAsset(pdfName).password((String)null).defaultPage(0).onPageError((OnPageErrorListener)(new OnPageErrorListener() {
         public final void onPageError(int page, Throwable $noName_1) {
            Toast.makeText((Context)PdfViewActivity.this, (CharSequence)("Error at page: " + page), 1).show();
         }
      })).load();
   }

   private final void selectPdfFromStorage() {
      Toast.makeText((Context)this, (CharSequence)"selectPDF", 1).show();
      Intent browseStorage = new Intent("android.intent.action.GET_CONTENT");
      browseStorage.setType("application/pdf");
      browseStorage.addCategory("android.intent.category.OPENABLE");
      this.startActivityForResult(Intent.createChooser(browseStorage, (CharSequence)"Select PDF"), 99);
   }

   private final void showPdfFromUri(Uri uri) {
      ((PDFView)this._$_findCachedViewById(id.pdfView)).fromUri(uri).defaultPage(0).spacing(10).load();
   }

   private final void showPdfFromFile(File file) {
      ((PDFView)this._$_findCachedViewById(id.pdfView)).fromFile(file).password((String)null).defaultPage(0).enableSwipe(true).swipeHorizontal(false).enableDoubletap(true).onPageError((OnPageErrorListener)(new OnPageErrorListener() {
         public final void onPageError(int page, Throwable $noName_1) {
            Toast.makeText((Context)PdfViewActivity.this, (CharSequence)("Error at page: " + page), 1).show();
         }
      })).load();
   }

   private final void downloadPdfFromInternet(String url, final String dirPath, final String fileName) {
      PRDownloader.download(url, dirPath, fileName).build().start((OnDownloadListener)(new OnDownloadListener() {
         public void onDownloadComplete() {
            Toast.makeText((Context)PdfViewActivity.this, (CharSequence)"downloadComplete", 1).show();
            File downloadedFile = new File(dirPath, fileName);
            ProgressBar var10000 = (ProgressBar)PdfViewActivity.this._$_findCachedViewById(id.progressBar);
            Intrinsics.checkExpressionValueIsNotNull(var10000, "progressBar");
            var10000.setVisibility(8);
            PdfViewActivity.this.showPdfFromFile(downloadedFile);
         }

         public void onError(@Nullable Error error) {
            Toast.makeText((Context)PdfViewActivity.this, (CharSequence)("Error in downloading file : " + error), 1).show();
         }
      }));
   }

   private final void checkPdfAction(Intent intent) {
      String var10000 = intent.getStringExtra("ViewType");
      if (var10000 != null) {
         String var2 = var10000;
         switch(var2.hashCode()) {
         case -1884274053:
            if (var2.equals("storage")) {
               this.selectPdfFromStorage();
            }
            break;
         case -1408207997:
            if (var2.equals("assets")) {
               this.showPdfFromAssets(FileUtils.INSTANCE.getPdfNameFromAssets());
            }
            break;
         case 570410817:
            if (var2.equals("internet")) {
               ProgressBar var4 = (ProgressBar)this._$_findCachedViewById(id.progressBar);
               Intrinsics.checkExpressionValueIsNotNull(var4, "progressBar");
               var4.setVisibility(0);
               String fileName = "myFile.pdf";
               this.downloadPdfFromInternet(FileUtils.INSTANCE.getPdfUrl(), FileUtils.INSTANCE.getRootDirPath((Context)this), fileName);
            }
         }
      }

   }

   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      if (requestCode == 99 && resultCode == -1 && data != null) {
         Uri selectedPdfFromStorage = data.getData();
         this.showPdfFromUri(selectedPdfFromStorage);
      }

   }

   public View _$_findCachedViewById(int var1) {
      if (this._$_findViewCache == null) {
         this._$_findViewCache = new HashMap();
      }

      View var2 = (View)this._$_findViewCache.get(var1);
      if (var2 == null) {
         var2 = this.findViewById(var1);
         this._$_findViewCache.put(var1, var2);
      }

      return var2;
   }

   public void _$_clearFindViewByIdCache() {
      if (this._$_findViewCache != null) {
         this._$_findViewCache.clear();
      }

   }

   @Metadata(
      mv = {1, 1, 18},
      bv = {1, 0, 3},
      k = 1,
      d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u0005"},
      d2 = {"Lcom/mindorks/example/openpdffile/PdfViewActivity$Companion;", "", "()V", "PDF_SELECTION_CODE", "", "app_debug"}
   )
   public static final class Companion {
      private Companion() {
      }

      // $FF: synthetic method
      public Companion(DefaultConstructorMarker $constructor_marker) {
         this();
      }
   }
}


 */