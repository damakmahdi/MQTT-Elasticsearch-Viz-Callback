# MQTT-Elasticsearch-Viz-Callback
First of all, this project ensures the Callback management of IOT Data of energy consumption collected via MQTT protocol.
Listeners will handle the reception of MQTT messages and automatically call Bulk-Index API from elasticsearch for the saving purpose.
Then, the next step will be retrieveing data back from elasticsearch, processing/preparing them in order to send them to HTTPServlets.
These servlets will be deployed on Tomcat(9.0) Web container and transferred to the browser as a WebService.
For the Data Viz, we have used Chart.js.
Note that all data & statistics(ExtendedStats API from elasticsearch) are near realtime.
