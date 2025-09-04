package com.example.cineviaapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class WatchLaterAdapter extends RecyclerView.Adapter<WatchLaterAdapter.ViewHolder> {

    private final List<Movie> movieList;
    private final Context context;

    public WatchLaterAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView moviePoster;
        TextView movieTitle, movieGenre;
        ImageButton btnWatchNow; // Bu butonun amacı neyse, onu koruyabiliriz

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.moviePoster);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            movieGenre = itemView.findViewById(R.id.movieGenre);
            btnWatchNow = itemView.findViewById(R.id.btnWatchNow);
        }
    }

    @NonNull
    @Override
    public WatchLaterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_watch_later, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WatchLaterAdapter.ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.movieTitle.setText(movie.getTitle());
        holder.movieGenre.setText(movie.getGenre());

        // **Önemli**: TMDB'den gelen posterPath'i kullanarak afiş URL'sini oluştur
        String posterPath = movie.getPosterPath();
        String fullPosterUrl = "";

        // posterPath'in geçerli bir değer olup olmadığını kontrol et
        if (posterPath != null && !posterPath.isEmpty() && !posterPath.equals("/placeholder.jpg")) {
            // TMDB afiş URL'si formatı: BASE_URL + SIZE + POSTER_PATH
            String baseUrl = "https://image.tmdb.org/t/p/";
            String imageSize = "w185"; // Afiş boyutu (örneğin w92, w154, w185, w342, w500, w780, original)
            fullPosterUrl = baseUrl + imageSize + posterPath;
        }

        Glide.with(context)
                .load(fullPosterUrl)
                .placeholder(R.drawable.image) // Afiş yüklenene kadar gösterilecek görsel
                .error(R.drawable.image_error) // Afiş yüklenirken hata oluşursa gösterilecek görsel
                .into(holder.moviePoster); // Afişi ImageView'a yerleştir

        // `btnWatchNow` için tıklama olayını yeniden etkinleştirebiliriz
        holder.btnWatchNow.setOnClickListener(v -> {
            // Örneğin, bu butona tıklayınca filmin detay sayfasına git
            Intent intent = new Intent(context, MovieDetail.class);
            intent.putExtra("movie_id", movie.getMovieId());
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("overview", movie.getOverview());
            intent.putExtra("poster_path", movie.getPosterPath());
            // Diğer bilgileri de istersen ekleyebilirsin:
            // intent.putExtra("genre", movie.getGenre());
            // intent.putExtra("poster_url", movie.getPosterUrl());
            context.startActivity(intent);
        });

        // Tüm öğe için tıklama olayını da yeniden etkinleştirebiliriz
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetail.class);
            intent.putExtra("movie_id", movie.getMovieId());
            intent.putExtra("title", movie.getTitle());
            intent.putExtra("overview", movie.getOverview());
            intent.putExtra("poster_path", movie.getPosterPath());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }
}