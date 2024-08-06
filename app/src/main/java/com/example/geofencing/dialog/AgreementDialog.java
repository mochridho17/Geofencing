package com.example.geofencing.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

public class AgreementDialog {
    private AgreementDialogListener listener;

    public AgreementDialog(AgreementDialogListener listener) {
        this.listener = listener;
    }

    public interface AgreementDialogListener {
        void onAgreementAccepted();
        void onAgreementRejected();
    }

    public static void showAgreementDialog(Context context, AgreementDialogListener listener) {
        ScrollView scrollView = new ScrollView(context);
        TextView textView = new TextView(context);

        String agreementText = "Perjanjian Penggunaan Lokasi - Mona (Monitoring Anak)\n\n" +
                "1. Pendahuluan\n" +
                "Selamat datang di Mona (Monitoring Anak). Dengan menggunakan aplikasi ini, Anda menyetujui perjanjian berikut yang mengatur penggunaan data lokasi anak Anda secara real-time. Mohon baca dengan cermat sebelum menggunakan aplikasi ini.\n\n" +
                "2. Penerimaan Perjanjian\n" +
                "Dengan mengunduh dan menggunakan aplikasi Mona (Monitoring Anak), Anda setuju untuk terikat oleh syarat dan ketentuan yang ditetapkan dalam perjanjian ini. Jika Anda tidak menyetujui perjanjian ini, harap jangan gunakan aplikasi ini.\n\n" +
                "3. Izin Penggunaan Lokasi\n" +
                "Aplikasi ini memerlukan akses ke lokasi anak Anda secara real-time untuk memonitor dan memberikan informasi lokasi terkini ke perangkat orang tua. Lokasi akan digunakan untuk tujuan keamanan dan pemantauan, dan tidak akan dibagikan dengan pihak ketiga tanpa izin Anda.\n\n" +
                "4. Pengumpulan dan Penggunaan Data\n" +
                "Data lokasi anak Anda akan dikumpulkan dan digunakan untuk:\n" +
                "   - Menyediakan informasi lokasi real-time kepada perangkat orang tua.\n" +
                "   - Memastikan keamanan anak Anda dengan memberikan fitur pemantauan lokasi.\n\n" +
                "Kami berkomitmen untuk melindungi privasi dan keamanan data anak Anda. Data lokasi hanya akan disimpan dan digunakan sesuai dengan kebijakan privasi kami.\n\n" +
                "5. Persetujuan Orang Tua\n" +
                "Dengan menggunakan aplikasi ini, Anda menyatakan bahwa Anda adalah orang tua atau wali sah dari anak yang akan dimonitor, dan Anda memiliki wewenang hukum untuk memberikan izin akses lokasi anak Anda.\n\n" +
                "6. Penghapusan Data Lokasi\n" +
                "Anda dapat kapan saja menghapus data lokasi anak Anda dengan menonaktifkan fitur lokasi di aplikasi atau menghapus akun Anda. Data lokasi yang tersimpan akan dihapus sesuai dengan kebijakan privasi kami.\n\n" +
                "7. Perubahan pada Perjanjian\n" +
                "Kami berhak untuk mengubah perjanjian ini kapan saja. Perubahan akan diberitahukan melalui aplikasi atau email. Dengan terus menggunakan aplikasi setelah perubahan tersebut, Anda dianggap menyetujui perjanjian yang telah diubah.\n\n" +
                "8. Kontak\n" +
                "Jika Anda memiliki pertanyaan atau kekhawatiran tentang perjanjian ini, silakan hubungi kami di moch.ridho17@gmail.com";

        textView.setText(agreementText);
        textView.setPadding(16, 16, 16, 16);
        scrollView.addView(textView);

        new AlertDialog.Builder(context)
                .setTitle("Perjanjian Penggunaan Lokasi - Mona (Monitoring Anak)")
                .setView(scrollView)
                .setPositiveButton("Setuju", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the agreement acceptance
                        if (listener != null) {
                            listener.onAgreementAccepted();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Tolak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Handle the agreement rejection
                        if (listener != null) {
                            listener.onAgreementRejected();
                        }
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }
}
