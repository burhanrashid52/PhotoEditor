FROM circleci/android:api-28-alpha
RUN yes | sdkmanager --licenses
RUN sudo mkdir -p photoeditor/.gradle
COPY ./ photoeditor/
RUN sudo chmod +x photoeditor/gradlew && sudo chown -R circleci:circleci photoeditor photoeditor/.gradle && sudo chmod -R 777 photoeditor photoeditor/.gradle
WORKDIR photoeditor
RUN ./gradlew androidDependencies
ENTRYPOINT ["/bin/bash", "-c", "/photoeditor/start.sh"]