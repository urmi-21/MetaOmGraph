# MetOmGraph
Memory effecient tool for plotting and analyzing large sets of data

# Link to download stable release
http://metnetweb.gdcb.iastate.edu/MetNet_MetaOmGraph.htm

# Overview

 MetaOmGraph is written in Java (JRE 1.4 or later required), so it is compatible with any Java-supported platform, including Windows, OS X, and Linux. MetaOmGraph can handle any sort of data that is stored in a delimited text file. Although designed to work with 'omics data (transcriptomics, proteomics, metabolomics, etc), MetaOmGraph is flexible enough to accommodate storing, manipulating, plotting and analyzing other biological data or, in fact, voluminous data of nearly any kind.

MetaOmGraph is a component of the emerging MetNet bioinformatics platform. MetNet is designed to explore Arabidopsis and other experimental data in the light of the regulatory and metabolic networks in that species. The platform includes: MetNetDB (a repository of gene/protein/metabolite annotations and network information obtained from web resources and expert input); FCModeler (a network modeling tool); and PathBinder and PubMed Assistant (text mining tools for the MEDLINE database).

The most important advantage of MetaOmGraph is its minimal memory usage, accomplished by indexing the data file, rather than storing the data in memory. A standard analysis program requires eight bytes of memory for EACH numerical entry in the data. In contrast, MetaOmGraph only stores the indices of the start of each row of data. Thus, only 8 bytes of memory are required per row (rather than 8 bytes of memory per number). Rows are read from disk as needed. Memory use is decreased proportional to the number of columns (e.g., for 10,000 samples, memory is decreased by 10,000-fold). This method is slower and somewhat more cumbersome to program, but is an excellent way to decrease the memory required for analysis of large data sets. 
   
MetaOmGraph is currently in beta. It utilizes libraries from the following projects:

   * BrowserLauncher2
   * Jakarta POI
   * JDOM
   * JFreeChart
