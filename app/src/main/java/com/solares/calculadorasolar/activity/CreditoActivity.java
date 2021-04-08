package com.solares.calculadorasolar.activity;

import androidx.appcompat.app.AppCompatActivity;

import com.solares.calculadorasolar.R;
import com.solares.calculadorasolar.classes.AutoSizeText;
import com.solares.calculadorasolar.classes.CalculadoraOnGrid;
import com.solares.calculadorasolar.classes.Constants;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import static com.solares.calculadorasolar.activity.MainActivity.GetPhoneDimensions;

public class CreditoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credito);

        Intent intent = getIntent();
        final CalculadoraOnGrid calculadora = (CalculadoraOnGrid) intent.getSerializableExtra(Constants.EXTRA_CALCULADORAON);

        //Pegando informações sobre o dispositivo, para regular o tamanho da letra (fonte)
        //Essa função pega as dimensões e as coloca em váriaveis globais
        GetPhoneDimensions(this);

        Button buttonInstagram = findViewById(R.id.button_instagram);
        AutoSizeText.AutoSizeButton(buttonInstagram, MainActivity.alturaTela, MainActivity.larguraTela, 3f);

        Button buttonRecalcular = findViewById(R.id.button_recalcular);
        AutoSizeText.AutoSizeButton(buttonRecalcular, MainActivity.alturaTela, MainActivity.larguraTela, 3f);

        TextView textConheca = findViewById(R.id.text_conheca);
        AutoSizeText.AutoSizeTextView(textConheca, MainActivity.alturaTela, MainActivity.larguraTela, 3f);

        //Colocando as imagens nos image views
        //Imagem do instagram
        ImageView imageInstagram = findViewById(R.id.imageViewInstagram);
        imageInstagram.setImageDrawable(getResources().getDrawable(R.drawable.instagram_icon));

        //Imagem com o nome do Solares
        ImageView imageTitulo = findViewById(R.id.imageViewCredito);
        imageTitulo.setImageDrawable(getResources().getDrawable(R.drawable.retangulo_logo_solares));

        //Imagem do Social
        ImageView imageSocial = findViewById(R.id.imageViewCirculoSocial);
        imageSocial.setImageDrawable(getResources().getDrawable(R.drawable.circulo_social));

        //Imagem do Barco
        ImageView imageBarco = findViewById(R.id.imageViewCirculoBarco);
        imageBarco.setImageDrawable(getResources().getDrawable(R.drawable.circulo_barco));

        //Se o usuário clicar no ícone do instagram ou no botão com o @, ele é redirecionado para o instagram do solares
        imageInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/projeto.solares/?utm_source=SolaresOn&utm_medium=name&utm_campaign=app"));
                startActivity(intent);
            }
        });
        buttonInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/projeto.solares/?utm_source=SolaresOn&utm_medium=icon&utm_campaign=app"));
                startActivity(intent);
            }
        });

        //Se o usuário clicar no botão recalcular, limpa-se todas as activities e volta à activity MainActivity
        buttonRecalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FinalizarActivity(calculadora);
            }
        });
    }

    public void FinalizarActivity(CalculadoraOnGrid calculadora){
        Intent intent = new Intent(this, MainActivity.class);
        //Isso limpa as activities já abertas
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
