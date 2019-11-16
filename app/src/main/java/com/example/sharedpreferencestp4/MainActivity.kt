package com.example.sharedpreferencestp4
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import org.jetbrains.anko.startActivityForResult

class MainActivity : AppCompatActivity() {

     /*on garde un ID du dernier element ajouté au fichier de preferences
      pour parcourir celuis ci par la suite a chaque creation de l 'appli*/

    var ID=0
    var personnes =  mutableListOf<Personne>()
    companion object {
        const val EXTRA_ISCONFIRMED = "ConfirmationActivity.ISCONFIRMED"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.sharedpreferencestp4.R.layout.activity_main)
        buildRecyclerView()

        //le fichier sharedPreferences qui va contenir la liste des contacts
        val objetShredPrefences=this?.getPreferences(Context.MODE_PRIVATE)

        //on recup la valeur de l'ID si c'est la premiére ouverture de lappli le mettre a 0
        ID=objetShredPrefences.getInt("DERNIER", 0)

        if (ID!=0){
        for (a in 1..ID step 1) {

            val nouvValeurnomSP = objetShredPrefences.getString("NOM{$a}", "personneNOM")
            val nouvValeuremailSP = objetShredPrefences.getString("EMAIL{$a}", "personneEMAIL")
            val nouvValeurtelSP = objetShredPrefences.getString("TEL{$a}", "personneTEL")
            val nouvValeurfixeSP = objetShredPrefences.getString("FIXE{$a}", "personneFIXE")
            if ((nouvValeurnomSP is String) && (nouvValeuremailSP is String) && (nouvValeurtelSP is String) && (nouvValeurfixeSP is String)) {
                var p9 =Personne(nouvValeurnomSP, nouvValeuremailSP, nouvValeurtelSP, nouvValeurfixeSP)
                personnes.add(0, p9)
            }
        }

            personnes.sortWith(compareBy({ it.nom }))
            buildRecyclerView()
            mon_recycler.adapter?.notifyItemInserted(0)
        }

        //le bouton pour permettre la saisie d'un contact
        btn_ajouter.setOnClickListener {
            //incrementer l'id a l'ajout d'un contact le décrementer en cas d'annulation
            ID++
            val editeur=objetShredPrefences.edit()
            editeur.putInt("DERNIER",ID)
            editeur.commit()
            startActivityForResult<AjoutPersonne>(1)


        }

    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> {
                // Résultat de startActivityForResult<ModifierActivity>
                if(resultCode == Activity.RESULT_OK){
                    val valider = data?.getBooleanExtra(AjoutPersonne.EXTRA_VALIDER, false) ?: false
                    if(valider){
                        // L'utilisateur a utilisé le bouton "valider"
                        // On récupère la valeur dans l'extra (avec une valeur par défaut de "")
                        val nouvValeurnom = data?.getStringExtra(AjoutPersonne.EXTRA_NOM) ?: ""
                        val nouvValeuremail = data?.getStringExtra(AjoutPersonne.EXTRA_EMAIL) ?: ""
                        val nouvValeurtel = data?.getStringExtra(AjoutPersonne.EXTRA_TEL) ?: ""
                        val nouvValeurfixe = data?.getStringExtra(AjoutPersonne.EXTRA_FAXE) ?: ""

                        var p8=Personne(nouvValeurnom,nouvValeuremail,nouvValeurtel,nouvValeurfixe)


                        val objetShredPrefences=this?.getPreferences(Context.MODE_PRIVATE)

                        val editeur=objetShredPrefences.edit()

                        editeur.putString("NOM{$ID}",nouvValeurnom)
                        editeur.putString("EMAIL{$ID}",nouvValeuremail)
                        editeur.putString("TEL{$ID}",nouvValeurtel )
                        editeur.putString("FIXE{$ID}",nouvValeurfixe)
                        editeur.commit()


                        personnes.add(0,p8)
                        //cette ligne permet de trier la liste des contactes par ordre alphabetique
                        personnes.sortWith(compareBy({it.nom}))
                        buildRecyclerView()
                        mon_recycler.adapter?.notifyItemInserted(0)

                    }else{
                        //ID--
                    }
                }else if(resultCode == Activity.RESULT_CANCELED){
                    // L'utilisateur a utilisé le bouton retour <- de son téléphone
                    // on ne fait rien de spécial non plus
                }
            }
        }
    }


    fun buildRecyclerView() {
        mon_recycler.setHasFixedSize(true)
        //mon_recycler.setAdapter(mAdapter)
        mon_recycler.layoutManager = LinearLayoutManager(this)

        mon_recycler.adapter = PersonneAdapter(personnes.toTypedArray())
        {
            //ici on affiche juste toutes les informations dans un Toast
            //on aurait tres bien pu les passer en parametre avec un intent et les afficher dans une autre activity
            Toast.makeText(this, "Element selectionné: ${it}", Toast.LENGTH_LONG).show()
            var  nom="${it.nom}"
            var  tel="${it.tel}"
            var  mail="${it.email}"
            var  faxe="${it.fixe}"
            val intent3 = Intent(this, AfficheDetailActivity::class.java)
            intent3.putExtra("NOM",nom)
            intent3.putExtra("TEL",tel)
            intent3.putExtra("MAIL",mail)
            intent3.putExtra("FAXE",faxe)
            startActivity(intent3)


        }


    }



}