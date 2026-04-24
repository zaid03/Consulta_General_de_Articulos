package com.example.backend.dto;

public class ArticleProjectionDTO implements ArticleProjection {
    private String afacod;
    private String afa_afades;
    private String asucod;
    private String asu_asudes;
    private String artcod;
    private String artdes;
    private String artref;
    private Integer artblo;
    private Double artuni;
    private Double artsol;
    private Double artrec;
    private String aun_aundes;
    private Double artuco;
    private Double artuem;
    private Double artpmi;
    private Double artpmp;
    private Double artmin;
    private Double artopt;

    public ArticleProjectionDTO(String afacod, String afa_afades, String asucod, String asu_asudes,
            String artcod, String artdes, String artref, Integer artblo, Double artuni, Double artsol,
            Double artrec, String aun_aundes, Double artuco, Double artuem, Double artpmi, Double artpmp,
            Double artmin, Double artopt) {
        this.afacod = afacod;
        this.afa_afades = afa_afades;
        this.asucod = asucod;
        this.asu_asudes = asu_asudes;
        this.artcod = artcod;
        this.artdes = artdes;
        this.artref = artref;
        this.artblo = artblo;
        this.artuni = artuni;
        this.artsol = artsol;
        this.artrec = artrec;
        this.aun_aundes = aun_aundes;
        this.artuco = artuco;
        this.artuem = artuem;
        this.artpmi = artpmi;
        this.artpmp = artpmp;
        this.artmin = artmin;
        this.artopt = artopt;
    }

    @Override
    public String getAFACOD() { return afacod; }
    @Override
    public String getAfa_AFADES() { return afa_afades; }
    @Override
    public String getASUCOD() { return asucod; }
    @Override
    public String getAsu_ASUDES() { return asu_asudes; }
    @Override
    public String getARTCOD() { return artcod; }
    @Override
    public String getARTDES() { return artdes; }
    @Override
    public String getARTREF() { return artref; }
    @Override
    public Integer getARTBLO() { return artblo; }
    @Override
    public Double getARTUNI() { return artuni; }
    @Override
    public Double getARTSOL() { return artsol; }
    @Override
    public Double getARTREC() { return artrec; }
    @Override
    public String getAun_AUNDES() { return aun_aundes; }
    @Override
    public Double getARTUCO() { return artuco; }
    @Override
    public Double getARTUEM() { return artuem; }
    @Override
    public Double getARTPMI() { return artpmi; }
    @Override
    public Double getARTPMP() { return artpmp; }
    @Override
    public Double getARTMIN() { return artmin; }
    @Override
    public Double getARTOPT() { return artopt; }
}