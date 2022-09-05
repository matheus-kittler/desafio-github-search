package br.com.igorbag.githubsearch.ui

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.data.GitHubService
import br.com.igorbag.githubsearch.databinding.ActivityMainBinding
import br.com.igorbag.githubsearch.domain.Repository
import br.com.igorbag.githubsearch.ui.adapter.RepositoryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var githubApi: GitHubService
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupRetrofit()
        setupListeners()
    }

    //metodo responsavel por configurar os listeners click da tela
    private fun setupListeners() {
        //@TODO - colocar a acao de click do botao confirmar
        val prefs: SharedPreferences = getSharedPreferences("key", MODE_PRIVATE)
        binding.btnConfirm.setOnClickListener {
            saveUserLocal(binding.etUserGithub.text)
            showUserName()
            getAllReposByUserName(prefs.getString("user-name", ""))
        }
    }


    // salvar o usuario preenchido no EditText utilizando uma SharedPreferences
    private fun saveUserLocal(name: Editable?) {
        // Persistir o usuario preenchido na editText com a SharedPref no listener do botao salvar
        val prefs: SharedPreferences = getSharedPreferences("key", MODE_PRIVATE)

        val editor: SharedPreferences.Editor = prefs.edit()

        editor.putString("user-name", name.toString())

        editor.commit()
    }

    private fun showUserName() {
        //depois de persistir o usuario exibir sempre as informacoes no EditText  se a sharedpref possuir algum valor, exibir no proprio editText o valor salvo

        val prefs: SharedPreferences = getSharedPreferences("key", MODE_PRIVATE)
        var codeName: String

        binding.etUserGithub.setOnEditorActionListener {_, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                codeName = prefs.getString("user-name", "").toString()
            }
            true
        }

        //TODO nao entendi muito bem o que era para fazer nesta funcao mas coloquei algo
    }

    //Metodo responsavel por fazer a configuracao base do Retrofit
    private fun setupRetrofit() {
        /*
           realizar a Configuracao base do retrofit
           Documentacao oficial do retrofit - https://square.github.io/retrofit/
           URL_BASE da API do  GitHub= https://api.github.com/
           lembre-se de utilizar o GsonConverterFactory mostrado no curso
        */
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        githubApi = retrofit.create(GitHubService::class.java)

    }

    //Metodo responsavel por buscar todos os repositorios do usuario fornecido
    private fun getAllReposByUserName(githubName: String?) {
        // TODO 6 - realizar a implementacao do callback do retrofit e chamar o metodo setupAdapter se retornar os dados com sucesso
        if (githubName != null) {
            binding.pbList.visibility = View.VISIBLE
            githubApi.getAllRepositoriesByUser(githubName)
                .enqueue(object : Callback<List<Repository>> {
                    override fun onResponse(
                        call: Call<List<Repository>>,
                        response: retrofit2.Response<List<Repository>>
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                binding.pbList.visibility = View.GONE
                                setupAdapter(it)
                            }
                        } else {
                            binding.pbList.visibility = View.GONE
                            setupEmptyList(call.timeout().toString())
                        }
                    }

                    override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                        binding.pbList.visibility = View.GONE
                        setupEmptyList(t.message)
                    }

                })
        }
    }

    // Metodo responsavel por realizar a configuracao do adapter
    fun setupAdapter(list: List<Repository>) {
        /*
            Implementar a configuracao do Adapter , construir o adapter e instancia-lo
            passando a listagem dos repositorios
         */

        val repoAdapter = RepositoryAdapter(list)
        binding.rvRepositories.apply {
            adapter = repoAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        }

        repoAdapter.btnShareLister = { share, _ ->
            shareRepositoryLink(share.htmlUrl)
        }
        repoAdapter.cardItemLister = { url, _ ->
            openBrowser(url.htmlUrl)
        }
    }

    fun setupEmptyList(error: String?) {
        binding.rvRepositories.visibility = View.GONE
        binding.tvListEmpty.visibility = View.VISIBLE
        binding.tvListEmpty.text = error
    }


    // Metodo responsavel por compartilhar o link do repositorio selecionado
    // Colocar esse metodo no click do share item do adapter
    private fun shareRepositoryLink(urlRepository: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, urlRepository)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    // Metodo responsavel por abrir o browser com o link informado do repositorio

    // Colocar esse metodo no click item do adapter
    private fun openBrowser(urlRepository: String) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(urlRepository)
            )
        )
    }

}