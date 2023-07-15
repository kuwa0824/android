#!/bin/bash

export APPID=io.github.kuwa0824.eyer

export APPNM=`echo ${APPID} | sed "s/.*\.//"`
export APPCC=`echo ${APPID} | sed "s/\./_/g"`
export SRCDIR=`echo ${APPID} | sed "s/\./\//g"`

mv settings.gradle a.txt
sed "s/eyel/${APPNM}/" a.txt > settings.gradle
rm a.txt

cd app
for i in AndroidManifest.xml build.gradle; do
    mv $i a.txt
    sed "s/io.github.kuwa0824.eyel/${APPID}/" a.txt > $i
done
rm a.txt

cd jni
for i in DetectionBasedTracker_jni.cpp DetectionBasedTracker_jni.h; do
    mv $i a.txt
    sed "s/io_github_kuwa0824_eyel/${APPCC}/" a.txt > $i
done
rm a.txt

cd ../res/values
mv strings.xml a.txt
sed "s/Eye\(Left\)/${APPNM}/" a.txt > strings.xml
rm a.txt

cd ../../src/main
mkdir -p ${SRCDIR}
for i in FdActivity.java DetectionBasedTracker.java; do
    mv io/github/kuwa0824/eyel/$i ${SRCDIR}/$i
done
cd ${SRCDIR}
for i in FdActivity.java DetectionBasedTracker.java; do
    mv $i a.txt
    sed "s/io.github.kuwa0824.eyel/${APPID}/" a.txt > $i
done
rm a.txt

