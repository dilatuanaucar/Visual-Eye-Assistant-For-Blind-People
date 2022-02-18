# Visual-Eye-Assistant-For-Blind-People
Object Detection on Android camera with TensorFlow

Bu projede, COCO veri kümesinde eğitilmiş nicelenmiş bir MobileNet SSD modeli kullanarak cihazınızın arka kamerası tarafından görülen çerçevelerdeki nesneleri 
(sınırlayıcı kutular ve sınıflar) sürekli olarak algılayan bir kamera uygulamasıdır . Bu talimatlar, demoyu bir Android cihazda oluşturma ve çalıştırma konusunda 
size yol gösterir.

Android Studio'yu kullanarak demo oluşturulur.
Nesne Tanıma için

Tensorflow için gerekli olan nesne dedektörleri entegre edildi.
Java'da çıkarımı çalıştırıldı.
Adım 1: Gradle bağımlılığını ve diğer ayarları içe aktarın
Kopya .tflite modeli idare edilecek Android modülün varlıkları dizinine model dosyası. Dosya sıkıştırılmış gerektiğini belirtin ve modülün için 
TensorFlow Lite kütüphanesini eklemek build.gradle dosyası:
android {
    // Other settings

    // Specify tflite file should not be compressed for the app apk
    aaptOptions {
        noCompress "tflite"
    }

}

dependencies {
    // Other dependencies

    // Import the Task Vision Library dependency (NNAPI is included)
    implementation 'org.tensorflow:tensorflow-lite-task-vision:0.3.0'
    implementation 'org.tensorflow:tensorflow-lite-gpu-delegate-plugin:0.3.0'
}
Adım 2: Modeli kullanma

Bu örnek, özel bir model kullanarak TensorFlow Lite nesne algılamayı nasıl gerçekleştireceğinizi gösterir.
 git clone https://github.com/tensorflow/models/
* Bu depoyu oluşturun ve kurun. cd models/research python3 setup.py build && python3 setup.py install* Open Images v4'te eğitilmiş MobileNet 
SSD'yi buradan indirin . Önceden eğitilmiş TensorFlow model dosyalarını çıkarın. * models/research Dizine gidin ve donmuş TensorFlow Lite 
grafiğini almak için bu kodu çalıştırın. python3 object_detection/export_tflite_ssd_graph.py \
 --pipeline_config_path object_detection/samples/configs/ssd_mobilenet_v2_oid_v4.config \
 --trained_checkpoint_prefix <directory with ssd_mobilenet_v2_oid_v4_2018_12_12>/model.ckpt \
 --output_directory exported_model* Dondurulmuş grafiği TFLite modeline dönüştürün. tflite_convert \ 
--input_shape=1,300,300,3 \ --input_arrays=normalized_input_image_tensor \
 --output_arrays=TFLite_Detection_PostProcess,TFLite_Detection_PostProcess:1,TFLite_Detection_PostProcess:2,TFLite_Detection_PostProcess:3 \ 
--allow_custom_ops \ --graph_def_file=exported_model/tflite_graph.pb \
 --output_file=<directory with the TensorFlow examples repository>/lite/examples/object_detection/android/app/src/main/assets/detect.tflite 
input_shape=1,300,300,3çünkü eğitilmiş  model yalnızca bu girdi şekliyle çalışır.


allow_custom_ops TFLite_Detection_PostProcess işlemine izin vermek için gereklidir.

input_arraysve output_arraysörnek algılama modelinin görselleştirilmiş grafiğinden çizilebilir.bazel run //tensorflow/lite/tools:visualize \ 
"<directory with the TensorFlow examples repository>/lite/examples/object_detection/android/app/src/main/assets/detect.tflite" \ detect.html

class-descriptions-boxable'ın ikinci sütunundan labelmap.txt dosyasını alındı.
DetectorActivity.java'da TF_OD_API_IS_QUANTIZED değerini false olarak ayarlandı.

Ses Tanımlama için

Gradlepublic class YourActivity extends Activity {

    Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_layout);

        Speech.init(this, getPackageName());
    }

    @Override
    protected void onDestroy() {
        // prevent memory leaks when activity is destroyed
        Speech.getInstance().shutdown();
    }
}

Konuşma tanıma
Bir aktivitenin içinde:
Speech recognition
Inside an activity:
implementation 'net.gotev:speech:x.y.z'

Initialization
public class YourActivity extends Activity {

    Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_layout);

        Speech.init(this, getPackageName());
    }

    @Override
    protected void onDestroy() {
        // prevent memory leaks when activity is destroyed
        Speech.getInstance().shutdown();
    }
}

Konuşma tanıma
Bir aktivitenin içinde:
try {
    // you must have android.permission.RECORD_AUDIO granted at this point
    Speech.getInstance().startListening(new SpeechDelegate() {
        @Override
        public void onStartOfSpeech() {
            Log.i("speech", "speech recognition is now active");
        }

        @Override
        public void onSpeechRmsChanged(float value) {
            Log.d("speech", "rms is now: " + value);
        }

        @Override
        public void onSpeechPartialResults(List<String> results) {
            StringBuilder str = new StringBuilder();
            for (String res : results) {
                str.append(res).append(" ");
            }

            Log.i("speech", "partial result: " + str.toString().trim());
        }

        @Override
        public void onSpeechResult(String result) {
            Log.i("speech", "result: " + result);
        }
    });
} catch (SpeechRecognitionNotAvailable exc) {
    Log.e("speech", "Speech recognition is not available on this device!");
    // You can prompt the user if he wants to install Google App to have
    // speech recognition, and then you can simply call:
    //
    // SpeechUtil.redirectUserToGoogleAppOnPlayStore(this);
    //
    // to redirect the user to the Google App page on Play Store
} catch (GoogleVoiceTypingDisabledException exc) {
    Log.e("speech", "Google voice typing must be enabled!");
}

Kaynakları yayınla
Aktivitenin onDestroy'unda şunu eklendi:

@Override
protected void onDestroy() {
    Speech.getInstance().shutdown();
}

SpeechProgressView'ın düzgün çalışması için her zaman bir LinearLayout içinde olması önemlidir. Genişliği ve yüksekliği, çubuk yükseklik ayarlarına göre ayarlanabilir. 

Daha sonra, konuşma tanımayı başlattığınızda, aynı zamanda SpeechProgressView:
Speech.getInstance().startListening(speechProgressView, speechDelegate);

Yapılandırma
Geçerli yerel ayarı ve sesi alındı.
Speech.getInstance().getSpeechToTextLanguage() ve Speech.getinstance().getTextToSpeechVoice() kullanıldı.
