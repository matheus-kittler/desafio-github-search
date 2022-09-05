package br.com.igorbag.githubsearch.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.igorbag.githubsearch.databinding.RepositoryItemBinding
import br.com.igorbag.githubsearch.domain.Repository

class RepositoryAdapter(private val repositories: List<Repository>) :
    RecyclerView.Adapter<RepositoryAdapter.ViewHolder>() {

    var cardItemLister: ((Repository, Int) -> Unit)? = null
    var btnShareLister: ((Repository, Int) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RepositoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    // Pega o conteudo da view e troca pela informacao de item de uma lista
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Realizar o bind do viewHolder
        //Exemplo de Bind
        //  holder.preco.text = repositories[position].atributo

        // Exemplo de click no item
        //holder.itemView.setOnClickListener {
        // carItemLister(repositores[position])
        //}

        // Exemplo de click no btn Share
        //holder.favorito.setOnClickListener {
        //    btnShareLister(repositores[position])
        //}

        with(holder) {
            with(repositories[position]) {
                binding.tvNameRepo.text = name
                binding.ivFavorite.setOnClickListener {
                    btnShareLister?.invoke(this.copy(), position)
                }
                binding.cvCard.setOnClickListener {
                    cardItemLister?.invoke(this.copy(), position)
                }
            }
        }
    }

    // Pega a quantidade de repositorios da lista
    // realizar a contagem da lista
    override fun getItemCount(): Int = repositories.size

    inner class ViewHolder(val binding: RepositoryItemBinding)
        :RecyclerView.ViewHolder(binding.root)
}


