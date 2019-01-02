#!/usr/bin/env Rscript

#author: urmi
#creates heatmap
library(readr)
library("gplots")
args = commandArgs(trailingOnly=TRUE)

if (length(args)<2) {
  stop("Arguments must be supplied", call.=FALSE)
} 

#read data file
infile<-"C://Users//mrbai//Documents//GitHub//metaomgraph//aa.txt"
infile<-args[1]
outfile<-args[2]
dataHeatMap <- read_delim(infile,"\t", escape_double = FALSE, trim_ws = TRUE)
dataHeatMap_df<-as.data.frame(dataHeatMap)
rownames(dataHeatMap_df)<-dataHeatMap_df$Name
dataHeatMap_df$Name<-NULL
dataHeatMap_mat<-as.matrix(dataHeatMap_df)
#plot heatmap
#heatmap(dataHeatMap_mat,Rowv = NA,Colv = NA, col = cm.colors(256),cexRow = 1,cexCol = 1,scale = "column")
#save as .png file
png(filename=outfile,width = 900,height = 1000)

#gplots::heatmap.2(dataHeatMap_mat,scale = "column",Rowv = NA,Colv = NA,density.info = "none",margins = c(12, 12),trace = "none",col = cm.colors(256))
gplots::heatmap.2(dataHeatMap_mat,scale = "column",density.info = "none",margins = c(12, 12),trace = "none",col = cm.colors(256),
                  main = "Heatmap", xlab = "Runs",ylab = "",
                  lhei = c(2,8),keysize = 1,key.title = "sds",key.xlab = "Values",key.xtickfun = function(){})
dev.off()

