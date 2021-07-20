#docker exec -it polar-keycloak bash
#cd /opt/jboss/keycloak/bin
#add user

./kcadm.sh config credentials \
--server http://localhost:8080/auth \
--realm master \
--user admin \
--password password

#criar um helm para aplicação
./kcadm.sh create realms -s realm=PolarBookshop -s enabled=true

#Criando roles
./kcadm.sh create roles -r PolarBookshop -s name=employee
./kcadm.sh create roles -r PolarBookshop -s name=customer

#Criando usuarios

./kcadm.sh create users -r PolarBookshop \
-s username=isabelle \
-s firstName=Isabelle \
-s lastName=Dahl \
-s enabled=true

./kcadm.sh add-roles -r PolarBookshop --uusername isabelle --rolename employee --rolename customer

./kcadm.sh create users -r PolarBookshop \
-s username=bjorn \
-s firstName=Bjorn \
-s lastName=Vinterberg \
-s enabled=true

./kcadm.sh add-roles -r PolarBookshop --uusername bjorn --rolename customer


./kcadm.sh set-password -r PolarBookshop --username isabelle --new-password password
./kcadm.sh set-password -r PolarBookshop --username bjorn --new-password password

#Adicionando um client
./kcadm.sh create clients -r PolarBookshop \
-s clientId=edge-service \
-s enabled=true \
-s publicClient=false \
-s secret=polar-keycloak-secret \
-s 'redirectUris=["http://localhost:9000", "http://localhost:9000/login/oauth2/code/"]'