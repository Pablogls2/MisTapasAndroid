package com.example.mistapas.ui.listabares;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mistapas.R;
import com.example.mistapas.ui.modelos.Bar;

import java.util.ArrayList;

public class BaresAdapter  extends RecyclerView.Adapter<BaresAdapter.ViewHolder>{

    private ArrayList<Bar> lista;
    private Context context;
    private Bundle b;
    private FragmentManager fm;

    public BaresAdapter(ArrayList<Bar> lista, Context context, FragmentManager fm) {
        this.lista = lista;
        this.context = context;
        this.fm = fm;
    }



    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.bar, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }


    public void onBindViewHolder(ViewHolder holder, final int position) {
        String stars="";
        final Bar bar = (Bar) lista.get(position);
        if (bar.getNombre().length() > 50) {
            String recorte = bar.getNombre().substring(0, 50);
            holder.nombre.setText(recorte + "...");
        } else {
            holder.nombre.setText(bar.getNombre());
        }

        for (int i=0; i < bar.getEstrellas();i++){
            stars = stars+'*';
        }
        holder.estrellas.setText(stars);

       holder.imageView.setImageBitmap(Bitmap.createScaledBitmap(base64ToBitmap(bar.getImagen()),620,540,false));


        holder.cvJuegoCardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DetalleBares ij= DetalleBares.newInstance(bar);
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.nav_host_fragment,ij);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private Bitmap base64ToBitmap(String b64) {
        byte[] imageAsBytes = Base64.decode(b64.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }


    public int getItemCount() {
        return lista.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // componentes que vamos a manejar
        public ImageView imageView;
        public TextView nombre;
        public TextView estrellas;

        // Layout de la fila
        public CardView cvJuegoCardView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.ivListBaresImagen);
            this.nombre = (TextView) itemView.findViewById(R.id.tvListaNombre);
            this.estrellas = (TextView) itemView.findViewById(R.id.tvListaEstrellas);
            cvJuegoCardView = (CardView) itemView.findViewById(R.id.cvListJuegos);
        }
    }
}
