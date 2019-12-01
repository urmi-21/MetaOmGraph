#!/usr/bin/env Rscript

#author: urmi
#A script to create heatmap. Invoked through MetaOmGraph.

library(dplyr)
library(plyr)
library(scales)
library(readr)
library(data.table)
library(ggplot2)

args = commandArgs(trailingOnly=TRUE)

if (length(args)<2) {
  stop("Invalid Arguments", call.=FALSE)
} 

#read data
infile<-args[1]
metadataFile<-args[2]
outDir<-args[3]

data<-read_delim(infile,"\t", escape_double = FALSE, trim_ws = TRUE)
rownames(data)<-data$Name
data$Name<-NULL
tdata<-as.data.frame(t(data))
tdata$Name<-rownames(tdata)
data.melt<-melt(tdata)
data.melt <- ddply(data.melt, .(variable), transform, rescale = rescale(value))
data.melt$Name <- factor(data.melt$Name, levels=unique(as.character(data.melt$Name)) )
data.melt<-data.melt[order(data.melt$Name),]


plotHeight<-ncol(data)/10
pdf(paste(outDir,.Platform$file.sep,"heatmap.pdf",sep = ""),height = plotHeight,width = 10)

ggplot(data.melt, aes(variable, Name)) + geom_tile(aes(fill = rescale),colour = "white") + 
  scale_fill_gradient(low = "white", high = "red")+
  theme(axis.text.x = element_text(angle = 90, hjust = 1))+
  theme(panel.grid.major = element_blank(), panel.grid.minor = element_blank(),panel.background = element_blank(), axis.line = element_line(colour = "black"))
  




dev.off()

#Recomended to save the session info for reproducibility details
#Create a file sessionInfo.txt and save the R sessionInfo()
writeLines(capture.output(sessionInfo()), paste(outDir,.Platform$file.sep,"sessionInfo.txt",sep = ""))
