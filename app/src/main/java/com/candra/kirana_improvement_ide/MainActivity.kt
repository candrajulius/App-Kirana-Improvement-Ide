package com.candra.kirana_improvement_ide

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import com.candra.kirana_improvement_ide.databinding.ActivityMainBinding
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object{
        const val REQUEST_CODE_CAMERA2 = 2
        const val REQUEST_CODE_GALLERY2 = 4
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.sharePdf.isEnabled = false

        binding.sharePdf.setOnClickListener {
            sharePdf()
        }


        binding.cameraSesudahButton.setOnClickListener {
            takeAPictureWithCamera2()
        }


        binding.storageGallerySesudahButton.setOnClickListener {
            takeAPictureWithGallery2()
        }

        //permission
        checkPermission()


        createDateCalendar()

        setTollbar()
    }



    private fun takeAPictureWithCamera2(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAMERA2)
    }


    private fun takeAPictureWithGallery2(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_GALLERY2)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                REQUEST_CODE_CAMERA2 -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.gambarSesudah.setImageBitmap(bitmap)

                    binding.cetakPdf.setOnClickListener {
                        allData(bitmap)
                    }
                }
                REQUEST_CODE_GALLERY2 -> {
                    val uri = data?.data
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,uri)
                    binding.gambarSesudah.setImageBitmap(bitmap)
                    binding.cetakPdf.setOnClickListener {
                        allData(bitmap)
                    }
                }
            }
        }
    }


    private fun setTollbar(){
        supportActionBar?.title = resources.getString(R.string.app_name)
    }

    private fun checkPermission(){
        Dexter.withContext(this@MainActivity)
            .withPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener{
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0?.areAllPermissionsGranted() == true){
                        Toast.makeText(this@MainActivity,"permission diizinkan",Toast.LENGTH_SHORT).show()
                    }else{
                        showDialogPermissionGranted()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            }).onSameThread().check()
    }

    private fun createDateCalendar(){
        val calendar = Calendar.getInstance()

        val datePicker = DatePickerDialog.OnDateSetListener { datePicker, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR,year)
            calendar.set(Calendar.MONTH,month)
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth)
            val myFormat = "dd/MM/yyyy"
            val sdf = SimpleDateFormat(myFormat,Locale.UK)
            binding.editTanggal.setText(sdf.format(calendar.time))
        }
        binding.editTanggal.setOnClickListener {
            DatePickerDialog(this,datePicker,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

    }

    private fun allData(bitmap1: Bitmap){
        binding.apply {

            val perusahaan = perusahaan.editText?.text.toString()
            val pencetusIde = pencetusIde.editText?.text.toString()
            val anggota1 = anggotaOne.editText?.text.toString()
            val anggota2 = anggotaTwo.editText?.text.toString()
            val judul = judul.editText?.text.toString()
            val departemen = departemen.editText?.text.toString()
            val bagian = bagian.editText?.text.toString()
            val noRegistrasi = noRegistrasi.editText?.text.toString()
            val uraianMasalah = uraianMasalah.editText?.text.toString()
            val standarSeharusnya = standardSeharusnya.editText?.text.toString()
            val pemborosan = pemborosan.editText?.text.toString()
            val ide = ide.editText?.text.toString()
            val dilakukan = apaYangDilakukan.editText?.text.toString()

            val kondisiSebelum = kondisiSebelum.editText?.text.toString()
            val tanggalImplementasiIde = tglImplementasiIde.editText?.text.toString()
            val biayaImprovement = biayaImrpovement.editText?.text.toString()
            val benefit = benefit.editText?.text.toString()
            val standard = standart.editText?.text.toString()
            val kondisiSesudah = kondisiSesudah.editText?.text.toString()
            val gambarSesudahError = binding.gambarSesudah.drawable.constantState
            val temptErrorSesudah = getDrawable(R.drawable.ic_baseline_error_outline_24)?.constantState

            if (perusahaan.isEmpty()){
                binding.perusahaan.error = "Perusahaan masih kosong"
                binding.perusahaan.isErrorEnabled = true
                binding.perusahaan.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(pencetusIde.isEmpty()){
                binding.pencetusIde.error = "Pencetus ide masih kosong"
                binding.pencetusIde.isErrorEnabled = true
                binding.pencetusIde.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(anggota1.isEmpty()){
                binding.anggotaOne.error = "Anggota 1 masih kosong"
                binding.anggotaOne.isErrorEnabled = true
                binding.anggotaOne.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if (anggota2.isEmpty()){
                binding.anggotaTwo.error = "Anggota 2 masih kosong"
                binding.anggotaTwo.isErrorEnabled = true
                binding.anggotaTwo.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(judul.isEmpty()){
                binding.judul.error = "Judul masih kosong"
                binding.judul.isErrorEnabled = true
                binding.judul.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(departemen.isEmpty()){
                binding.departemen.error = "Departemen masih kosong"
                binding.departemen.isErrorEnabled = true
                binding.departemen.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(bagian.isEmpty()){
                binding.bagian.error = "Bagian masih kosong"
                binding.bagian.isErrorEnabled = true
                binding.bagian.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(noRegistrasi.isEmpty()){
                binding.noRegistrasi.error = "No Registrasi masih kosong"
                binding.noRegistrasi.isErrorEnabled = true
                binding.noRegistrasi.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(uraianMasalah.isEmpty()){
                binding.uraianMasalah.error = "Uraian masalah masih kosong"
                binding.uraianMasalah.isErrorEnabled = true
                binding.uraianMasalah.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(standarSeharusnya.isEmpty()){
                binding.standardSeharusnya.error = "Standard seharusnya masih kosong"
                binding.standardSeharusnya.isErrorEnabled = true
                binding.standardSeharusnya.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(pemborosan.isEmpty()){
                binding.pemborosan.error = "Pemborosan masih kosong"
                binding.pemborosan.isErrorEnabled = true
                binding.pemborosan.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(ide.isEmpty()){
                binding.ide.error = "ide masih kosong"
                binding.ide.isErrorEnabled = true
                binding.ide.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(dilakukan.isEmpty()){
                binding.apaYangDilakukan.error = "Apa yang dilakukan kosong"
                binding.apaYangDilakukan.isErrorEnabled = true
                binding.apaYangDilakukan.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(kondisiSebelum.isEmpty()){
                binding.kondisiSebelum.error = "Kondisi sebelum masih kosong"
                binding.kondisiSebelum.isErrorEnabled = true
                binding.kondisiSebelum.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(tanggalImplementasiIde.isEmpty()){
                binding.tglImplementasiIde.error = "Tanggal implementasi ide masih kosong"
                binding.tglImplementasiIde.isErrorEnabled = true
                binding.tglImplementasiIde.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(biayaImprovement.isEmpty()){
                binding.biayaImrpovement.error = "Biaya improvement masih kosong"
                binding.biayaImrpovement.isErrorEnabled = true
                binding.biayaImrpovement.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(benefit.isEmpty()){
                binding.benefit.error = "Benefit masih kosong"
                binding.benefit.isErrorEnabled = true
                binding.benefit.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(standard.isEmpty()){
                binding.standart.error = "Standard masih kosong"
                binding.standart.isErrorEnabled = true
                binding.standart.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(kondisiSesudah.isEmpty()){
                binding.kondisiSesudah.error = "Kondisi sesudah masih kosong"
                binding.kondisiSesudah.isErrorEnabled = true
                binding.kondisiSesudah.setErrorTextColor(ColorStateList.valueOf(Color.RED))
            }else if(gambarSesudahError == temptErrorSesudah){
                Help.showToast("Anda tidak mengupload foto sesudah",this@MainActivity)
            }
            else{
                cetakToPdf(perusahaan,pencetusIde,anggota1,anggota2,judul,departemen,bagian,noRegistrasi
                ,uraianMasalah,standarSeharusnya,pemborosan,ide,dilakukan,kondisiSebelum,tanggalImplementasiIde,biayaImprovement,
                benefit,standard,kondisiSesudah,bitmap1)
                cleanData()
            }
        }
    }

    private fun cleanData(){
        binding.apply {
            perusahaan.editText?.text?.clear()
            perusahaan.isErrorEnabled = false
            pencetusIde.editText?.text?.clear()
            pencetusIde.isErrorEnabled = false
            anggotaOne.editText?.text?.clear()
            anggotaOne.isErrorEnabled = false
            anggotaTwo.editText?.text?.clear()
            anggotaTwo.isErrorEnabled = false
            judul.editText?.text?.clear()
            judul.isErrorEnabled = false
            departemen.editText?.text?.clear()
            departemen.isErrorEnabled = false
            bagian.editText?.text?.clear()
            bagian.isErrorEnabled = false
            noRegistrasi.editText?.text?.clear()
            noRegistrasi.isErrorEnabled = false
            uraianMasalah.editText?.text?.clear()
            uraianMasalah.isErrorEnabled = false
            standardSeharusnya.editText?.text?.clear()
            standardSeharusnya.isErrorEnabled = false
            pemborosan.editText?.text?.clear()
            pemborosan.isErrorEnabled = false
            ide.editText?.text?.clear()
            ide.isErrorEnabled = false
            apaYangDilakukan.editText?.text?.clear()
            apaYangDilakukan.isErrorEnabled = false
            tglImplementasiIde.editText?.text?.clear()
            tglImplementasiIde.isErrorEnabled = false
            biayaImrpovement.editText?.text?.clear()
            biayaImrpovement.isErrorEnabled = false
            benefit.editText?.text?.clear()
            benefit.isErrorEnabled = false
            standart.editText?.text?.clear()
            standart.isErrorEnabled = false
            kondisiSesudah.editText?.text?.clear()
            kondisiSesudah.isErrorEnabled = false
        }
    }

    private fun showDialogPermissionGranted() {
        AlertDialog.Builder(this)
            .setMessage("Aplikasi ini membutuhkan fitur perizinan dari sistem android anda" +
                    "Jika anda tidak mengaktifkan fiturnya maka aplikasi tidak dapat digunakan" +
                    "SIlahkan tekan tombol Peri Ke Setting untuk mengaktifkan perizinan")
            .setTitle("Peringatan")
            .setIcon(R.mipmap.ic_launcher_foreground)
            .setPositiveButton("Pergi Ke Setting"){_,_ ->

                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",packageName,null)
                    intent.data = uri
                    startActivity(intent)
                }catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                    Log.d("MainActvity", "showDialogPermissionGranted: " + e.message.toString())
                }

            }

            .setNegativeButton("CANCEL"){dialog,_ ->
                dialog.dismiss()
                exitProcess(0)
            }.show()
    }

    private fun cetakToPdf(perushaan: String,pencetusIde: String,anggota1: String,anggota2: String,
    judul: String,departemen: String,bagian: String,noRegistrasi: String,uraianMasalah: String,
    standardSeharusnya: String,pemborosan: String,ide: String,dilakukan: String,kondisiSebelum: String,
    tanggalImpelementasiIde: String,biayaImprovement: String,benefit: String,standard: String,kondisiSesudah: String,bitmap1: Bitmap) {


            val path: String =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString()

            val simpelDateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(
            Date())

            val file = File(path,"$simpelDateFormat ss_nsi.pdf")
            val outputStream = FileOutputStream(file)

            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            pdfDocument.defaultPageSize = PageSize.A4
            document.setMargins(0F, 0F, 0F, 0F)

            val paragraphTitle = Paragraph("KIRANA IMPROVEMENT IDE SENDIRI").setBold().setFontSize(24F).setTextAlignment(TextAlignment.CENTER)

            val waktu = SimpleDateFormat("HH:mm:ss a",Locale.getDefault()).format(Date())
            val paragraphTanggaldanWaktu = Paragraph("Tanggal : $simpelDateFormat dan waktu: $waktu").setFontSize(18F)


            val width = floatArrayOf(100F,100F,100F,100F,100F,100F,100F,100F)

            val paragraphProfile = Paragraph("PROFILE").setBold().setFontSize(20F).setTextAlignment(TextAlignment.LEFT).setMarginTop(10F)
            val updateSesudah = Paragraph("DOKUMENTASI IMPROVEMENT").setFontSize(18F).setTextAlignment(TextAlignment.CENTER).setMarginTop(5F).setBold()
            val tableProfile = Table(width)
            tableProfile.setHorizontalAlignment(HorizontalAlignment.LEFT)

            tableProfile.addCell(Cell().add(Paragraph("Perusahaan")))
            tableProfile.addCell(Cell().add(Paragraph("Pencetus Ide")))
            tableProfile.addCell(Cell().add(Paragraph("Anggota 1")))
            tableProfile.addCell(Cell().add(Paragraph("Anggota 2")))
            tableProfile.addCell(Cell().add(Paragraph("Judul")))
            tableProfile.addCell(Cell().add(Paragraph("Departemen")))
            tableProfile.addCell(Cell().add(Paragraph("Bagian")))
            tableProfile.addCell(Cell().add(Paragraph("No Registrasi")))


            tableProfile.addCell(Cell().add(Paragraph(perushaan).setFontSize(15F)))
            tableProfile.addCell(Cell().add(Paragraph(pencetusIde).setFontSize(15F)))
            tableProfile.addCell(Cell().add(Paragraph(anggota1)).setFontSize(15F))
            tableProfile.addCell(Cell().add(Paragraph(anggota2)).setFontSize(15F))
            tableProfile.addCell(Cell().add(Paragraph(judul)).setFontSize(15F))
            tableProfile.addCell(Cell().add(Paragraph(departemen)).setFontSize(15F))
            tableProfile.addCell(Cell().add(Paragraph(bagian)).setFontSize(15F))
            tableProfile.addCell(Cell().add(Paragraph(noRegistrasi)).setFontSize(15F))



            val width2 = floatArrayOf(100F,100F,100F)
            val tabelLatarBelakang = Table(width2)
            val planText = Paragraph("PLAN").setFontSize(20F).setTextAlignment(TextAlignment.LEFT).setBold().setMarginTop(10F)
            val plantText2 = Paragraph("Latar Belakang").setFontSize(16F).setTextAlignment(TextAlignment.LEFT).setBold().setMarginTop(5F)

            tabelLatarBelakang.addCell(Cell().add(Paragraph("Uraian Masalah")))
            tabelLatarBelakang.addCell(Cell().add(Paragraph("Standard Seharusnya")))
            tabelLatarBelakang.addCell(Cell().add(Paragraph("Pemborosan Biaya")))

            tabelLatarBelakang.addCell(Cell().add(Paragraph(uraianMasalah)).setFontSize(15F))
            tabelLatarBelakang.addCell(Cell().add(Paragraph(standardSeharusnya)).setFontSize(15F))
            tabelLatarBelakang.addCell(Cell().add(Paragraph(pemborosan)).setFontSize(15F))


            val planText3 = Paragraph("Usulan Perbaikan").setFontSize(16F).setTextAlignment(TextAlignment.LEFT).setBold().setMarginTop(5F)
            val widht3 = floatArrayOf(100F)
            val tableUsulanPerbaikan = Table(widht3)

            tableUsulanPerbaikan.addCell(Cell().add(Paragraph("Ide")))
            tableUsulanPerbaikan.addCell(Cell().add(Paragraph(ide)))

            val plantText4 = Paragraph("DO").setFontSize(20F).setTextAlignment(TextAlignment.LEFT).setBold().setMarginTop(5F)
            val childPlantText4 = Paragraph("Aktivitas Perbaikan").setFontSize(16F).setTextAlignment(TextAlignment.LEFT).setBold().setMarginTop(5F)

            val width4 = floatArrayOf(100F,100F,100F,100F)
            val tableDo = Table(width4)

            tableDo.addCell(Cell().add(Paragraph("Apa yang dilakukan")))
            tableDo.addCell(Cell().add(Paragraph("Kondisi Sebelum")))
            tableDo.addCell(Cell().add(Paragraph("Kondisi Sesudah")))
            tableDo.addCell(Cell().add(Paragraph("Tanggal Implementasi Ide")))

            tableDo.addCell(Cell().add(Paragraph(dilakukan)).setFontSize(15F))
            tableDo.addCell(Cell().add(Paragraph(kondisiSebelum)).setFontSize(15F))
            tableDo.addCell(Cell().add(Paragraph(kondisiSesudah)).setFontSize(15F))
            tableDo.addCell(Cell().add(Paragraph(tanggalImpelementasiIde)).setFontSize(15F))

            val plantText5 = Paragraph("CHECK").setFontSize(20F).setTextAlignment(TextAlignment.LEFT).setBold().setMarginTop(5F)
            val childPlantText5 = Paragraph("Uraian Biaya Improvement & Benefit").setFontSize(16F).setTextAlignment(TextAlignment.LEFT).setBold().setMarginTop(5F)

            val width5 = floatArrayOf(100F,100F)
            val tableCheck = Table(width5)

            tableCheck.addCell(Cell().add(Paragraph("Biaya Improvement")))
            tableCheck.addCell(Cell().add(Paragraph("Benefit")))

            tableCheck.addCell(Cell().add(Paragraph(biayaImprovement)).setFontSize(15F))
            tableCheck.addCell(Cell().add(Paragraph(benefit)).setFontSize(15F))

            val plantText6 = Paragraph("Bentuk Standarisasi Yang Dibuat").setFontSize(16F).setTextAlignment(TextAlignment.LEFT).setBold().setMarginTop(5F)
            val width6 = floatArrayOf(100F)
            val parentPlantext6 = Paragraph("ACT").setFontSize(20F).setTextAlignment(TextAlignment.LEFT).setBold().setMarginTop(5F)
            val tableStandard = Table(width6)
            tableStandard.addCell(Cell().add(Paragraph("Standard")))
            tableStandard.addCell(Cell().add(Paragraph(standard)).setFontSize(15F))

            val stream = ByteArrayOutputStream()
            bitmap1.compress(Bitmap.CompressFormat.PNG,100,stream)
            val byte = stream.toByteArray()

            val imageData = ImageDataFactory.create(byte)
            val image = Image(imageData).setHorizontalAlignment(HorizontalAlignment.CENTER).setWidth(150F).setHeight(150F)

            val nameLetter = "FRM-KM.CRP.38-02"
            val numberLetter = Paragraph("Nomor Surat: $nameLetter").setFontSize(16F).setTextAlignment(TextAlignment.LEFT).setBold().setMarginTop(500F)


            // create for title in pdf document and date
            document.add(paragraphTitle)
            document.add(paragraphTanggaldanWaktu)

            // create for profile in pdf document
            document.add(paragraphProfile)
            document.add(tableProfile)

            // create for plan in pdf document
            document.add(planText)
            document.add(plantText2)
            document.add(tabelLatarBelakang)

            document.add(planText3)
            document.add(tableUsulanPerbaikan)

            // create for do in pdf document
            document.add(plantText4)
            document.add(childPlantText4)
            document.add(tableDo)

            // create for check in pdf document
            document.add(plantText5)
            document.add(childPlantText5)
            document.add(tableCheck)

            // create for standarisasi in pdf document
            document.add(parentPlantext6)
            document.add(plantText6)
            document.add(tableStandard)
            document.add(updateSesudah)
            document.add(image)
            document.add(numberLetter)

            Toast.makeText(this,"Pdf has been created",Toast.LENGTH_SHORT).show()

            document.close()
            binding.sharePdf.isEnabled = true

            Help.helpDialog(this,"Silahkan cek di folder file lalu lihat folder download dan cari \n " +
                    "nama filenya ss_nsi.pdf","File yang anda cari berada di $file")

    }

    private fun sharePdf(){
        val simpelDateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(
            Date()
        )
        val tanggalDate: String = simpelDateFormat
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(path,"$tanggalDate ss_nsi.pdf")

        val pdfUri: Uri?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            pdfUri = FileProvider.getUriForFile(this,this.packageName + ".provider",file)
        }else{
            pdfUri = Uri.fromFile(file)
        }

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "application/pdf"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_STREAM,pdfUri)
        shareIntent.setPackage("com.whatsapp")
        startActivity(shareIntent)

    }


}