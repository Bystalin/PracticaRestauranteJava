/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.restaurante;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Byron-PC
 */
@Entity
@Table(name = "plato")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Plato.findAll", query = "SELECT p FROM Plato p")
    , @NamedQuery(name = "Plato.findById", query = "SELECT p FROM Plato p WHERE p.id = :id")
    , @NamedQuery(name = "Plato.findByNombre", query = "SELECT p FROM Plato p WHERE p.nombre = :nombre")
    , @NamedQuery(name = "Plato.findByDescripcion", query = "SELECT p FROM Plato p WHERE p.descripcion = :descripcion")
    , @NamedQuery(name = "Plato.findByPrecio", query = "SELECT p FROM Plato p WHERE p.precio = :precio")
    , @NamedQuery(name = "Plato.findByImagen", query = "SELECT p FROM Plato p WHERE p.imagen = :imagen")})
public class Plato implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "nombre")
    private String nombre;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "descripcion")
    private String descripcion;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "precio")
    private BigDecimal precio;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "imagen")
    private String imagen;
    @JoinColumn(name = "idcategoria", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Categoria idcategoria;

    public Plato() {
    }

    public Plato(Integer id) {
        this.id = id;
    }

    public Plato(Integer id, String nombre, String descripcion, BigDecimal precio, String imagen) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.imagen = imagen;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Categoria getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(Categoria idcategoria) {
        this.idcategoria = idcategoria;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Plato)) {
            return false;
        }
        Plato other = (Plato) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "test.restaurante.Plato[ id=" + id + " ]";
    }
    
}
