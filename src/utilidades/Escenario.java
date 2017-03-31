/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import figuras.Barrera;
import figuras.Caja;
import figuras.Salto;
import java.util.ArrayList;
import javax.media.j3d.BranchGroup;
import simulador.Figura;
import simulador.Juego;

/**
 *
 * @author Enrique Rios Santos
 */
public class Escenario{
    private BranchGroup conjunto;
    private ArrayList<Figura> listaObjetosFisicos;
    private Juego juego;
    private DiscreteDynamicsWorld mundoFisico;
    private float elasticidad=0f;
    private float masa=0;
    private float dumpingLineal=0.5f;
    
    public Escenario(BranchGroup conjunto, ArrayList<Figura> listaObjetosFisicos, Juego juego, DiscreteDynamicsWorld mundoFisico) {
        this.conjunto=conjunto;
        this.listaObjetosFisicos=listaObjetosFisicos;
        this.juego=juego;
        this.mundoFisico=mundoFisico;
        float altura=2f;
        
        recta(60,altura, 1);
        recta(60,altura, 2);
        curva(altura,1);
        curva(altura,2);
        salto();
        cajas();
    }

    private void curva(float altura, int i) {
        if(i==1){
            Barrera barrera1 = new Barrera(20, conjunto, listaObjetosFisicos, juego);
            barrera1.crearPropiedades(masa, elasticidad, dumpingLineal, 21f, altura, -80f, mundoFisico);

            Barrera barrera2 = new Barrera(8, conjunto, listaObjetosFisicos, juego);
            barrera2.aplicarRotacion(-(3f*(float)Math.PI)/4f,masa, elasticidad, dumpingLineal, -0.4f, altura, -67f, mundoFisico);

            Barrera barrera3 = new Barrera(55, conjunto, listaObjetosFisicos, juego);
            barrera3.aplicarRotacion(-(float)Math.PI/2f,masa, elasticidad, dumpingLineal, -35f, altura, -100f, mundoFisico);

            Barrera barrera4 = new Barrera(20, conjunto, listaObjetosFisicos, juego);
            barrera4.aplicarRotacion(-(float)Math.PI/2f,masa, elasticidad, dumpingLineal, -25f, altura, -72.5f, mundoFisico);

            Barrera barrera5 = new Barrera(8, conjunto, listaObjetosFisicos, juego);
            barrera5.aplicarRotacion((3f*(float)Math.PI)/4f,masa, elasticidad, dumpingLineal, -50f, altura, -66f, mundoFisico);

            Barrera barrera6 = new Barrera(20, conjunto, listaObjetosFisicos, juego);
            barrera6.crearPropiedades(masa, elasticidad, dumpingLineal, -90f, altura, -80f, mundoFisico);
        }else if(i==2){
            Barrera barrera1 = new Barrera(20, conjunto, listaObjetosFisicos, juego);
            barrera1.crearPropiedades(masa, elasticidad, dumpingLineal, 21f, altura, 80f, mundoFisico);

            Barrera barrera2 = new Barrera(8, conjunto, listaObjetosFisicos, juego);
            barrera2.aplicarRotacion((3f*(float)Math.PI)/4f,masa, elasticidad, dumpingLineal, -0.4f, altura, 67f, mundoFisico);

            Barrera barrera3 = new Barrera(55, conjunto, listaObjetosFisicos, juego);
            barrera3.aplicarRotacion(-(float)Math.PI/2f,masa, elasticidad, dumpingLineal, -35f, altura, 100f, mundoFisico);

            Barrera barrera4 = new Barrera(20, conjunto, listaObjetosFisicos, juego);
            barrera4.aplicarRotacion(-(float)Math.PI/2f,masa, elasticidad, dumpingLineal, -25f, altura, 72.5f, mundoFisico);

            Barrera barrera5 = new Barrera(8, conjunto, listaObjetosFisicos, juego);
            barrera5.aplicarRotacion(-(3f*(float)Math.PI)/4f,masa, elasticidad, dumpingLineal, -50f, altura, 66f, mundoFisico);

            Barrera barrera6 = new Barrera(20, conjunto, listaObjetosFisicos, juego);
            barrera6.crearPropiedades(masa, elasticidad, dumpingLineal, -90f, altura, 80f, mundoFisico);
        }
        
    }

    private void recta(float tam, float altura, int i) {
        Barrera barrera1 = new Barrera(tam, conjunto, listaObjetosFisicos, juego);
        Barrera barrera2 = new Barrera(tam, conjunto, listaObjetosFisicos, juego);
        
        if(i==1){
            barrera1.crearPropiedades(masa, elasticidad, dumpingLineal, 5f, altura, 0, mundoFisico);
            barrera2.crearPropiedades(masa, elasticidad, dumpingLineal,21f, altura, 0, mundoFisico);
        }else if(i==2){
            barrera1.crearPropiedades(masa, elasticidad, dumpingLineal,-55f, altura, 0, mundoFisico);
            barrera2.crearPropiedades(masa, elasticidad, dumpingLineal,-90f, altura, 0, mundoFisico);
        }
    }

    private void salto() {
        Salto rampa = new Salto(conjunto, listaObjetosFisicos, juego);
        rampa.aplicarRotacion(((float)Math.PI)/8f, masa, elasticidad, dumpingLineal,-65f, 0.7f, 40f, mundoFisico);//-62

    }
    
    private void cajas(){
        float tam=1f;
        //grupo 1
        Caja caja11 = new Caja(tam, conjunto, listaObjetosFisicos, juego);
        caja11.crearPropiedades(0.1f, elasticidad, dumpingLineal, -17f, (tam/2f)+0.5f, -77.5f, mundoFisico);
        Caja caja12 = new Caja(tam, conjunto, listaObjetosFisicos, juego);
        caja12.crearPropiedades(0.1f, elasticidad, dumpingLineal, -17f, (tam/2f)+0.5f, -79.5f, mundoFisico);
        Caja caja13 = new Caja(tam, conjunto, listaObjetosFisicos, juego);
        caja13.crearPropiedades(0.1f, elasticidad, dumpingLineal, -17f, (3f*tam/2f)+1.3f, -78f, mundoFisico);
        
        //grupo 2
        Caja caja21 = new Caja(tam, conjunto, listaObjetosFisicos, juego);
        caja21.crearPropiedades(0.1f, elasticidad, dumpingLineal, -30f, (tam/2f)+0.5f, 78f, mundoFisico);
        Caja caja22 = new Caja(tam, conjunto, listaObjetosFisicos, juego);
        caja22.crearPropiedades(0.1f, elasticidad, dumpingLineal, -30f, (tam/2f)+0.5f, 80f, mundoFisico);
        Caja caja23 = new Caja(tam, conjunto, listaObjetosFisicos, juego);
        caja23.crearPropiedades(0.1f, elasticidad, dumpingLineal, -30f, (3f*tam/2f)+1.3f, 79f, mundoFisico);
    }
}
