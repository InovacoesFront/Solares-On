package com.solares.calculadorasolar.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.solares.calculadorasolar.R;
import com.solares.calculadorasolar.classes.AutoSizeText;
import com.solares.calculadorasolar.classes.Constants;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.Locale;

import static com.solares.calculadorasolar.activity.MainActivity.GetPhoneDimensionsAndSetTariff;
import static com.solares.calculadorasolar.activity.MainActivity.PtarifaPassada;

public class ConsumoActivity extends AppCompatActivity {

    public static float porcent = 3f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumo);

        //Pegando informações sobre o dispositivo, para regular o tamanho da letra (fonte)
        //Essa função pega as dimensões e as coloca em váriaveis globais
        GetPhoneDimensionsAndSetTariff(this, PtarifaPassada);

        //Pega o layout para poder colocar um listener nele (esconder o teclado)
        ConstraintLayout layout = findViewById(R.id.layout_consumo);

        //Pega o view do título e ajusta o tamanho da fonte
        TextView textTituloConsumo = findViewById(R.id.text_titulo_consumo);
        AutoSizeText.AutoSizeTextView(textTituloConsumo, MainActivity.alturaTela, MainActivity.larguraTela, 4f);

        //Pega o view da explicação e ajusta o tamanho da fonte
        TextView textExplicacaoConsumo = findViewById(R.id.text_explicacao);
        AutoSizeText.AutoSizeTextView(textExplicacaoConsumo, MainActivity.alturaTela, MainActivity.larguraTela, porcent);

        //Pega o view da tarifa atual e ajusta o tamanho da fonte
        TextView textConsumoAtual = findViewById(R.id.text_consumo_atual);
        AutoSizeText.AutoSizeTextView(textConsumoAtual, MainActivity.alturaTela, MainActivity.larguraTela, porcent);

        //Pega o view da nova tarifa e ajusta o tamanho da fonte
        TextView textNovoConsumo = findViewById(R.id.text_novo_consumo);
        AutoSizeText.AutoSizeTextView(textNovoConsumo, MainActivity.alturaTela, MainActivity.larguraTela, porcent);

        //Pega o view do edit text para a nova tarifa e ajusta o tamanho da fonte
        final EditText editConsumo = findViewById(R.id.editText_consumo);
        AutoSizeText.AutoSizeEditText(editConsumo, MainActivity.alturaTela, MainActivity.larguraTela, porcent);

        //Pega o view da unidade e ajusta o tamanho da fonte
        TextView textUnidadeTarifa = findViewById(R.id.text_unidade_consumo);
        AutoSizeText.AutoSizeTextView(textUnidadeTarifa, MainActivity.alturaTela, MainActivity.larguraTela, porcent);

        //Pega o view do botão pra recalcular e ajusta o tamanho da fonte
        Button buttonRecalcConsumo = findViewById(R.id.button_recalcular_consumo);
        AutoSizeText.AutoSizeButton(buttonRecalcConsumo, MainActivity.alturaTela, MainActivity.larguraTela, 4f);

        //Pega o view do botão para voltar e ajusta o tamanho da fonte
        Button buttonVoltar = findViewById(R.id.button_voltar);
        AutoSizeText.AutoSizeButton(buttonVoltar, MainActivity.alturaTela, MainActivity.larguraTela, 4f);


        //pegar os intents
        Intent intent = getIntent();
        final double custoReais = intent.getDoubleExtra(Constants.EXTRA_CUSTO_REAIS, 0.0);
        final String[] cityVec = intent.getStringArrayExtra(Constants.EXTRA_VETOR_CIDADE);
        final String cityName = intent.getStringExtra(Constants.EXTRA_CIDADE);
        final double consumoAtualKWh = intent.getDoubleExtra(Constants.EXTRA_CONSUMO, 0.0);

        //atualizar o tarifa atual
        textConsumoAtual.setText(String.format(Locale.ITALY, "Consumo atual: %.2f kWh", consumoAtualKWh));

        //Listener do botão de recalcular
        buttonRecalcConsumo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AtualizarTarifa(custoReais, cityVec, cityName, editConsumo);
            }
        });

        //Listeners do botão de voltar
        buttonVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //Listener do fundo do layout, se o usuário clicar nele, esconde o teclado
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(editConsumo);
            }
        });
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null){
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } else {
            Log.i("TarifaActivity", "Error while hiding keyboard");
        }
    }

    public void AtualizarTarifa(final double custoReais, final String[] cityVec, final String cityName, final EditText editConsumo){
        try {
            //Pega o consumo digitada no edit text
            double novoConsumo = Double.parseDouble(editConsumo.getText().toString());
            double novaTarifa, consumoSemImpostos;

            //Se o consumo for menor ou igual a CIP, pede pro usuário inserir novamente
            if (novoConsumo <= Constants.CIP) {
                Toast.makeText(this, "O valor para o consumo está muito baixo!", Toast.LENGTH_LONG).show();
            } else {
                ///////Descobre a nova tarifa baseada no consumo em KWh
                //Tira os impostos do custo em reais
                consumoSemImpostos = MainActivity.ValueWithoutTaxes(custoReais);
                //Descobre a tarifa
                novaTarifa = consumoSemImpostos/novoConsumo;
                //Atualiza a tarifa passada
                MainActivity.PtarifaPassada = novaTarifa;
                //Refaz o cálculo com a nova tarifa/consumo e inicia a ResultadoActivity
                //Criar uma thread para fazer o cálculo pois é um processamento demorado
                Thread thread = new Thread(){
                    public void run(){
                        MainActivity.Calculate(-1, cityVec, cityName, -1, custoReais, null, ConsumoActivity.this);
                    }
                };
                thread.start();

            }
        } catch (Exception e){
            Toast.makeText(this, "Insira um novo consumo, com um ponto separando a parte real da inteira", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
