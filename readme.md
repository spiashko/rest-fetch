

### TODO
- integrate with security (make it depended on JsonView) to forbidden filter and include relations which are not to meant to be presented
- try to apply JSON:API concept
- add swagger integration

### implementation thoughts:
 - JSON:API concept should be applied if we are going to include relations but if we are going to include on level of
aggregation root then there is no need in JSON:API concept 
 - BUT looks like it will be easier to create that JSON:API wrapper anyway

### usage thoughts
 - as this something that configured on each separate endpoint then it means that for some tricky case we can always
write endpoint in our usual way with dto and other boring things


